/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree
 */
package org.roda.core.plugins;

import com.google.common.collect.Iterables;
import jersey.repackaged.com.google.common.collect.Lists;
import org.roda.core.CorporaConstants;
import org.roda.core.RodaCoreFactory;
import org.roda.core.TestsHelper;
import org.roda.core.common.iterables.CloseableIterable;
import org.roda.core.common.monitor.TransferredResourcesScanner;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.v2.common.OptionalWithCause;
import org.roda.core.data.v2.index.IndexResult;
import org.roda.core.data.v2.index.filter.Filter;
import org.roda.core.data.v2.index.filter.SimpleFilterParameter;
import org.roda.core.data.v2.index.select.SelectedItemsList;
import org.roda.core.data.v2.index.sublist.Sublist;
import org.roda.core.data.v2.ip.*;
import org.roda.core.data.v2.ip.metadata.DescriptiveMetadata;
import org.roda.core.data.v2.ip.metadata.OtherMetadata;
import org.roda.core.data.v2.jobs.Job;
import org.roda.core.data.v2.jobs.PluginType;
import org.roda.core.index.IndexService;
import org.roda.core.index.IndexTestUtils;
import org.roda.core.model.ModelService;
import org.roda.core.plugins.base.ingest.IArxiuToAIPPlugin;
import org.roda.core.storage.fs.FSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Test(groups = {RodaConstants.TEST_GROUP_ALL, RodaConstants.TEST_GROUP_DEV, RodaConstants.TEST_GROUP_TRAVIS})
public class IArxiuToAIPPluginTest {

  private static final int CORPORA_FILES_COUNT = 4;
  private static final int CORPORA_FOLDERS_COUNT = 2;
  private static Path basePath;

  private static ModelService model;
  private static IndexService index;

  private static Path corporaPath;

  private static final Logger LOGGER = LoggerFactory.getLogger(IArxiuToAIPPluginTest.class);

  @BeforeMethod
  public void setUp() throws Exception {
    basePath = TestsHelper.createBaseTempDir(getClass(), true);

    boolean deploySolr = true;
    boolean deployLdap = true;
    boolean deployFolderMonitor = true;
    boolean deployOrchestrator = true;
    boolean deployPluginManager = true;
    boolean deployDefaultResources = false;
    RodaCoreFactory.instantiateTest(deploySolr, deployLdap, deployFolderMonitor, deployOrchestrator,
      deployPluginManager, deployDefaultResources, false);
    model = RodaCoreFactory.getModelService();
    index = RodaCoreFactory.getIndexService();

    URL corporaURL = IArxiuToAIPPluginTest.class.getResource("/corpora");
    corporaPath = Paths.get(corporaURL.toURI());

    LOGGER.info("Running internal plugins tests under storage {}", basePath);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    IndexTestUtils.resetIndex();
    RodaCoreFactory.shutdown();
    FSUtils.deletePath(basePath);
  }

  private TransferredResource createCorpora() throws Exception {

    final TransferredResourcesScanner f = RodaCoreFactory.getTransferredResourcesScanner();

    final Path sip = corporaPath.resolve(CorporaConstants.SIP_FOLDER).resolve(CorporaConstants.I_ARXIU_SIP);

    final TransferredResource transferredResourceCreated = f.createFile(null, CorporaConstants.I_ARXIU_SIP,
      Files.newInputStream(sip));

    index.commit(TransferredResource.class);
    return index.retrieve(TransferredResource.class, transferredResourceCreated.getUUID(), new ArrayList<>());
  }

  private AIP ingestCorpora() throws Exception {
    String aipType = RodaConstants.AIP_TYPE_MIXED;

    AIP root = model.createAIP(null, aipType, new Permissions(), RodaConstants.ADMIN);

    Map<String, String> parameters = new HashMap<>();
    parameters.put(RodaConstants.PLUGIN_PARAMS_PARENT_ID, root.getId());
    parameters.put(RodaConstants.PLUGIN_PARAMS_FORCE_PARENT_ID, "true");

    final TransferredResource transferredResource = createCorpora();
    AssertJUnit.assertNotNull(transferredResource);

    Job job = TestsHelper.executeJob(IArxiuToAIPPlugin.class, parameters, PluginType.SIP_TO_AIP,
      SelectedItemsList.create(TransferredResource.class, transferredResource.getUUID()));

    TestsHelper.getJobReports(index, job, true);

    index.commitAIPs();

    IndexResult<IndexedAIP> find = index.find(IndexedAIP.class,
      new Filter(new SimpleFilterParameter(RodaConstants.AIP_PARENT_ID, root.getId())), null, new Sublist(0, 10),
      new ArrayList<>());

    AssertJUnit.assertEquals(1L, find.getTotalCount());
    IndexedAIP indexedAIP = find.getResults().get(0);

    return model.retrieveAIP(indexedAIP.getId());
  }

  @Test
  public void testIngestIArxiuSIP() throws Exception {
    final AIP aip = ingestCorpora(); // ingest an iArxiu SIP (zip from local resources) returning the created AIP
    AssertJUnit.assertNotNull(aip);
    final String aipId = aip.getId();
    AssertJUnit.assertNotNull(aipId);

    final List<DescriptiveMetadata> descriptiveMetadataList = aip.getDescriptiveMetadata();
    AssertJUnit.assertNotNull(descriptiveMetadataList);
    AssertJUnit.assertNotSame(0, descriptiveMetadataList.size());
    final List<String> descriptiveMetadataTypes = descriptiveMetadataList.stream().map(descriptiveMetadata -> descriptiveMetadata.getType()).collect(Collectors.toList());
    AssertJUnit.assertTrue("Expected descriptive metadata 'DC' type not found: " + descriptiveMetadataTypes, descriptiveMetadataTypes.contains("DC"));
    AssertJUnit.assertTrue("Expected descriptive metadata 'Voc_expedient' type not found: " + descriptiveMetadataTypes, descriptiveMetadataTypes.contains("urn:iarxiu:2.0:vocabularies:cesca:Voc_expedient"));

    final List<OtherMetadata> otherMetadataList = getItems(model.listOtherMetadata(aipId, "OTHER", true));
    AssertJUnit.assertNotNull(otherMetadataList);
    /*AssertJUnit.assertNotSame(0, otherMetadataList.size());*/

    final List<Representation> representations = aip.getRepresentations();
    AssertJUnit.assertNotNull(representations);
    AssertJUnit.assertNotSame(0, representations.size());

    final List<File> reusableAllFiles = new ArrayList<>();
    for (Representation representation: representations) {
      AssertJUnit.assertNotNull(representation);

      final String representationId = representation.getId(); // BagIt retrieves the files from the AIP first representation id: aip.getRepresentations().get(0).getId()
      AssertJUnit.assertNotNull(representationId);
      final List<File> representationBinaryFiles = getItems(model.listFilesUnder(aipId,  representationId, true));
      AssertJUnit.assertNotSame(0, representationBinaryFiles.size());
      reusableAllFiles.addAll(representationBinaryFiles);

      final List<DescriptiveMetadata> repDescriptiveMetadataList = representation.getDescriptiveMetadata(); // BagIt retrieves the files from the AIP first representation id: aip.getRepresentations().get(0).getId()
      AssertJUnit.assertNotNull(repDescriptiveMetadataList);
      AssertJUnit.assertNotSame(0, repDescriptiveMetadataList.size());

      for (DescriptiveMetadata descriptiveMetadata: repDescriptiveMetadataList){
        final String metadataType = descriptiveMetadata.getType();
        AssertJUnit.assertNotNull(metadataType);
        AssertJUnit.assertTrue("Expected representation metadata not of 'DC' or 'Voc_document_exp' types: " + descriptiveMetadata,
                metadataType.equalsIgnoreCase("DC")
                        || metadataType.equalsIgnoreCase("urn:iarxiu:2.0:vocabularies:cesca:Voc_document_exp"));

        AssertJUnit.assertNotNull(descriptiveMetadata);
        final String foundDescriptiveMetadata = descriptiveMetadata.getId();
        /* descriptiveMetadata.getId() is not found;
         * the file is identified: fdb3d711-6c01-4934-8a95-8f57bb4ddbaf-index.xml-DOC_1.xml*/
        AssertJUnit.assertNotNull(foundDescriptiveMetadata);
        /* Model files not exposed: AssertJUnit.assertNotSame(0, representationDescriptiveMetadataFiles.size()); */
      }
      final List<OtherMetadata> repOtherMetadataList = getItems(model.listOtherMetadata(aipId, representationId));
      AssertJUnit.assertNotNull(repOtherMetadataList);
      /*AssertJUnit.assertNotSame(0, repOtherMetadataList.size());*/

    }

    // All folders and files...
    AssertJUnit.assertNotSame( 0, // the FOLDERS_COUNT + the FILES_COUNT: from SIP representation data: BIN_1/index.xml
            reusableAllFiles.size());
    // ...exists
    AssertJUnit.assertTrue(reusableAllFiles.stream().allMatch(file -> file != null));
  }

  private static <T> List<T> getItems(CloseableIterable<OptionalWithCause<T>> all) {
    final List<T> found = new ArrayList<>();
    Iterables.addAll(found, Lists.newArrayList(all).stream().filter(OptionalWithCause::isPresent)
            .map(OptionalWithCause::get).collect(Collectors.toList()));
    return found;
  }



}

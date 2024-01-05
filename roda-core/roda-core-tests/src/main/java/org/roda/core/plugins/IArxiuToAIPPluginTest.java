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
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RequestNotValidException;
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
import org.testng.Assert;
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

import static java.util.Arrays.asList;

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

  private TransferredResource createCorpora(String corporaFileNameSample) throws Exception {

    final TransferredResourcesScanner f = RodaCoreFactory.getTransferredResourcesScanner();

    final Path sip = corporaPath.resolve(CorporaConstants.SIP_FOLDER).resolve(corporaFileNameSample);

    final TransferredResource transferredResourceCreated = f.createFile(null, corporaFileNameSample,
      Files.newInputStream(sip));

    index.commit(TransferredResource.class);
    return index.retrieve(TransferredResource.class, transferredResourceCreated.getUUID(), new ArrayList<>());
  }

  private AIP ingestCorpora(String corporaFileNameSample) throws Exception {
    String aipType = RodaConstants.AIP_TYPE_MIXED;

    AIP root = model.createAIP(null, aipType, new Permissions(), RodaConstants.ADMIN);

    Map<String, String> parameters = new HashMap<>();
    parameters.put(RodaConstants.PLUGIN_PARAMS_PARENT_ID, root.getId());
    parameters.put(RodaConstants.PLUGIN_PARAMS_FORCE_PARENT_ID, "true");

    final TransferredResource transferredResource = createCorpora(corporaFileNameSample);
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
  public void testIngestIArxiuCesca1SIP() throws Exception {
    final AIP aip = ingestCorpora(CorporaConstants.I_ARXIU_CESCA1_SIP); // ingest an iArxiu SIP (zip from local resources) returning the created AIP
    final String aipId = verifyIngestedAip(aip);

    final  List<DescriptiveMetadata> descriptiveMetadataList = aip.getDescriptiveMetadata();
    verifyIngestedDescriptiveMetadata(aipId, descriptiveMetadataList,
            "DC", "urn:iarxiu:2.0:vocabularies:cesca:Voc_expedient");
    Assert.assertEquals(2, descriptiveMetadataList.size());

    final List<Representation> representations = aip.getRepresentations();
    verifyIngestedRepresentations(aipId, representations, "DC", "urn:iarxiu:2.0:vocabularies:cesca:Voc_document_exp");
    Assert.assertEquals(1, representations.size());

    verifyIngestedOtherFiles(aipId, 0); // does the search and verifies none (all used types are known)
  }

  @Test
  public void testIngestIArxiuCescaAppPreSIP() throws Exception {
    final AIP aip = ingestCorpora(CorporaConstants.I_ARXIU_CESCA_APP_PRE_SIP);
    final String aipId = verifyIngestedAip(aip);

    final  List<DescriptiveMetadata> descriptiveMetadataList = aip.getDescriptiveMetadata();
    verifyIngestedDescriptiveMetadata(aipId, descriptiveMetadataList,
            "urn:iarxiu:2.0:vocabularies:cesca:Voc_expedient", "urn:iarxiu:2.0:vocabularies:cesca:Voc_UPF");
    Assert.assertEquals(2, descriptiveMetadataList.size());

    final List<Representation> representations = aip.getRepresentations();
    verifyIngestedRepresentations(aipId, representations, "urn:iarxiu:2.0:vocabularies:cesca:Voc_document_exp");
    Assert.assertEquals(4, representations.size());

    verifyIngestedOtherFiles(aipId, 0); // does the search and verifies none (all used types are known)
  }

  private String verifyIngestedAip(AIP aip) {
    AssertJUnit.assertNotNull(aip);
    final String aipId = aip.getId();
    AssertJUnit.assertNotNull(aipId);
    return aipId;
  }

  private void verifyIngestedDescriptiveMetadata(String aipId, List<DescriptiveMetadata> descriptiveMetadataList, String... types) throws Exception {

    AssertJUnit.assertNotNull(descriptiveMetadataList);
    AssertJUnit.assertTrue(!descriptiveMetadataList.isEmpty());
    // verify the always present iArxiu expedient types
    expectDescriptiveMetadataTypes(descriptiveMetadataList, types);
    final List<File> binaryFiles = getItems(model.listFilesUnder(aipId,  null, true));
    AssertJUnit.assertNotNull(binaryFiles);
  }

  private void verifyIngestedRepresentations(String aipId, List<Representation> representations, String... expectedTypes) throws Exception {

    AssertJUnit.assertNotNull(representations);
    AssertJUnit.assertTrue(!representations.isEmpty());

    final List<String> expectedTypesList = new ArrayList<>(asList(expectedTypes));
    for (Representation representation: representations) {
      AssertJUnit.assertNotNull(representation);

      final String representationId = representation.getId(); // BagIt retrieves the files from the AIP first representation id: aip.getRepresentations().get(0).getId()
      AssertJUnit.assertNotNull(representationId);
      final List<File> representationBinaryFiles = getItems(model.listFilesUnder(aipId,  representationId, true));
      verifyBinaryFiles(representationBinaryFiles);

      final List<DescriptiveMetadata> repDescriptiveMetadataList = representation.getDescriptiveMetadata(); // BagIt retrieves the files from the AIP first representation id: aip.getRepresentations().get(0).getId()
      AssertJUnit.assertNotNull(repDescriptiveMetadataList);
      AssertJUnit.assertTrue(!repDescriptiveMetadataList.isEmpty());

      for (DescriptiveMetadata descriptiveMetadata: repDescriptiveMetadataList){
        final String metadataType = descriptiveMetadata.getType();
        AssertJUnit.assertNotNull(metadataType);
        AssertJUnit.assertTrue("Expected representation metadata type (" + asList(expectedTypes) + ") not matching '"+metadataType+"': " + descriptiveMetadata,
                asList(expectedTypes).contains(metadataType));
        expectedTypesList.remove(metadataType);

        AssertJUnit.assertNotNull(descriptiveMetadata);
        final String foundDescriptiveMetadata = descriptiveMetadata.getId();
        /* descriptiveMetadata.getId() is not found;
         * the file is identified: fdb3d711-6c01-4934-8a95-8f57bb4ddbaf-index.xml-DOC_1.xml*/
        AssertJUnit.assertNotNull(foundDescriptiveMetadata);
        /* Model files not exposed: AssertJUnit.assertNotEquals(0, representationDescriptiveMetadataFiles.size()); */
      }

    }

    Assert.assertEquals(0, expectedTypesList.size(), "Not all the expected types found: " + expectedTypesList);
  }

  private static void verifyBinaryFiles(List<File> binaryFiles){
    AssertJUnit.assertNotNull(binaryFiles);
    // All folders and files...
    AssertJUnit.assertTrue(!binaryFiles.isEmpty());// the FOLDERS_COUNT + the FILES_COUNT: as example from SIP representation data: BIN_1/index.xml

    // ...exists
    AssertJUnit.assertTrue(binaryFiles.stream().allMatch(file -> file != null));
  }

  private static void verifyIngestedOtherFiles(String aipId, int expectedNumber) throws AuthorizationDeniedException, RequestNotValidException, NotFoundException, GenericException {
    final List<OtherMetadata> otherMetadataList = getItems(model.listOtherMetadata(aipId, "OTHER", true));
    AssertJUnit.assertNotNull(otherMetadataList);
    AssertJUnit.assertTrue(expectedNumber == otherMetadataList.size());
  }

  private static List<String> expectDescriptiveMetadataTypes(List<DescriptiveMetadata> descriptiveMetadataList, String... expectedTypes){
    final List<String> descriptiveMetadataTypes = descriptiveMetadataList.stream().map(descriptiveMetadata -> descriptiveMetadata.getType()).collect(Collectors.toList());
    for (String expectedType: expectedTypes) {
      AssertJUnit.assertTrue("Expected descriptive metadata '"+expectedType+"' type not found: " + descriptiveMetadataTypes, descriptiveMetadataTypes.contains(expectedType));
    }
    return descriptiveMetadataTypes;
  }

  private static <T> List<T> getItems(CloseableIterable<OptionalWithCause<T>> all) {
    final List<T> found = new ArrayList<>();
    Iterables.addAll(found, Lists.newArrayList(all).stream().filter(OptionalWithCause::isPresent)
            .map(OptionalWithCause::get).collect(Collectors.toList()));
    return found;
  }



}

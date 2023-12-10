/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree
 */
package org.roda.core.plugins.base.ingest;

import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.exceptions.*;
import org.roda.core.data.v2.ip.AIP;
import org.roda.core.data.v2.ip.AIPState;
import org.roda.core.data.v2.ip.Permissions;
import org.roda.core.data.v2.ip.Representation;
import org.roda.core.data.v2.validation.ValidationException;
import org.roda.core.model.ModelService;
import org.roda.core.plugins.Plugin;
import org.roda.core.plugins.PluginHelper;
import org.roda.core.storage.ContentPayload;
import org.roda.core.storage.StringContentPayload;
import org.roda.core.storage.fs.FSPathContentPayload;
import org.roda_project.commons_ip.model.IPFile;
import org.roda_project.commons_ip.model.IPRepresentation;
import org.roda_project.commons_ip.model.SIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class IArxiuToAIPPluginUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(IArxiuToAIPPluginUtils.class);
  private static final String METADATA_TYPE = "key-value";
  private static final String METADATA_VERSION = null;

  private IArxiuToAIPPluginUtils() {
    // do nothing
  }

  public static AIP iArxiuSIPToAIP(SIP sip, String username, Permissions fullPermissions, ModelService model,
                                 List<String> ingestSIPIds, String ingestJobId, String parentId, String ingestSIPUUID, Plugin<?> plugin)
          throws RequestNotValidException, NotFoundException, GenericException, AlreadyExistsException,
          AuthorizationDeniedException, ValidationException, IOException, LockingException {

    AIPState state = AIPState.INGEST_PROCESSING;
    Permissions permissions = new Permissions();
    boolean notify = false;

    final String aipType = EARKSIPToAIPPluginUtils.getType(sip); // TODO redo in IArxiuToAIPPluginUtils or refactor to commonSIPToAIPPluginUtils

    AIP aip = model.createAIP(state, parentId, aipType, permissions, ingestSIPUUID, ingestSIPIds,
            ingestJobId, notify, username);

    PluginHelper.acquireObjectLock(aip, plugin); // TODO needed acquireObjectLock ?

    // process IP information
    EARKSIPToAIPPluginUtils.processIPInformation(model, sip, aip.getId(), notify, false); // TODO redo in IArxiuToAIPPluginUtils or refactor to commonSIPToAIPPluginUtils

    // process IPRepresentation information
    for (IPRepresentation representation : sip.getRepresentations()) {
      // TODO redo as from: commonSIPToAIPPluginUtils
      EARKSIPToAIPPluginUtils.processIPRepresentationInformation(model, representation, aip.getId(), notify, false, username, null);
    }

    // INFO 20190509 hsilva: this is required as the previous instructions
    // update the AIP metadata
    AIP createdAIP = model.retrieveAIP(aip.getId());

    // Set Permissions
    Permissions readPermissions = PermissionUtils.grantReadPermissionToUserGroup(model, createdAIP,
            aip.getPermissions());
    Permissions finalPermissions = PermissionUtils.grantAllPermissions(username, readPermissions, fullPermissions);
    createdAIP.setPermissions(finalPermissions);

    return model.updateAIP(createdAIP, username);
  }

  private // discarded implementation as iArxiu SIP implements the eARK METS format and not the BagIT SIP descriptive metadata
  static AIP iArxiuToAipFromGenerateDescriptiveMetadataFile(SIP iArxiu, ModelService model, String metadataFilename, List<String> ingestSIPIds,
    String ingestJobId, String computedParentId, String createdBy, Permissions permissions,
    String ingestSIPUUID) throws RequestNotValidException, NotFoundException, GenericException, AlreadyExistsException,
    AuthorizationDeniedException {

    final String metadataAsString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<metadata>\n" +
            "  <field name=\"simpledc.creator\">Creator</field>\n" +
            "  <field name=\"simpledc.publisher\">Publisher</field>\n" +
            "  <field name=\"simpledc.identifier\">Id</field>\n" +
            // "  <field name=\"Payload-Oxum\">5649120.4</field>\n" +
            // "  <field name=\"level\">item</field>\n" +
            // "  <field name=\"Bagging-Date\">2016-02-18</field>\n" +
            // "  <field name=\"Bag-Size\">5,4 MB</field>\n" +
            "  <field name=\"id\">"+iArxiu.getId()+"</field>\n" +
            "  <field name=\"title\">Title</field>\n" +
            "  <field name=\"simpledc.date\">"+iArxiu.getCreateDate()+"</field>\n" +
            "</metadata>\n"; // BagIt uses file from: sip.getDescriptiveMetadata().get(0).getMetadata().getPath());

    ContentPayload metadataAsPayload = new StringContentPayload(metadataAsString);

    AIPState state = AIPState.INGEST_PROCESSING;
    String aipType = RodaConstants.AIP_TYPE_MIXED;
    boolean notify = false;

    AIP aip = model.createAIP(state, computedParentId, aipType, permissions, ingestSIPUUID, ingestSIPIds,
      ingestJobId, notify, createdBy);

    model.createDescriptiveMetadata(aip.getId(), metadataFilename, metadataAsPayload, METADATA_TYPE, METADATA_VERSION,
      notify);

    boolean original = true;
    String representationType = RodaConstants.REPRESENTATION_TYPE_MIXED;

    for (IPRepresentation irep : iArxiu.getRepresentations()) {
      Representation rep = model.createRepresentation(aip.getId(), irep.getRepresentationID(), original,
        representationType, notify, createdBy);

      for (IPFile bagFile : irep.getData()) {
        ContentPayload payload = new FSPathContentPayload(bagFile.getPath());
        model.createFile(aip.getId(), rep.getId(), bagFile.getRelativeFolders(), bagFile.getFileName(), payload,
          notify);
      }
    }

    model.notifyAipCreated(aip.getId());
    return model.retrieveAIP(aip.getId());
  }

}

/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.plugins.base.ingest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.exceptions.AlreadyExistsException;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.LockingException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.v2.ip.AIP;
import org.roda.core.data.v2.ip.AIPState;
import org.roda.core.data.v2.ip.File;
import org.roda.core.data.v2.ip.IndexedAIP;
import org.roda.core.data.v2.ip.Permissions;
import org.roda.core.data.v2.ip.Representation;
import org.roda.core.data.v2.ip.metadata.PreservationMetadata.PreservationMetadataType;
import org.roda.core.data.v2.jobs.Report;
import org.roda.core.data.v2.validation.ValidationException;
import org.roda.core.model.ModelService;
import org.roda.core.plugins.Plugin;
import org.roda.core.plugins.PluginHelper;
import org.roda.core.storage.ContentPayload;
import org.roda.core.storage.StorageService;
import org.roda.core.storage.fs.FSPathContentPayload;
import org.roda.core.util.IdUtils;
import org.roda_project.commons_ip.model.IPDescriptiveMetadata;
import org.roda_project.commons_ip.model.IPFile;
import org.roda_project.commons_ip.model.IPMetadata;
import org.roda_project.commons_ip.model.IPRepresentation;
import org.roda_project.commons_ip.model.MetadataType;
import org.roda_project.commons_ip.model.RepresentationStatus;
import org.roda_project.commons_ip.model.SIP;
import org.roda_project.commons_ip.model.MetadataType.MetadataTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EARKSIPToAIPPluginUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(EARKSIPToAIPPluginUtils.class);

  private EARKSIPToAIPPluginUtils() {
    // do nothing
  }

  public static AIP earkSIPToAIP(SIP sip, String username, Permissions fullPermissions, ModelService model,
    List<String> ingestSIPIds, String ingestJobId, Optional<String> parentId, String ingestSIPUUID, Plugin<?> plugin)
    throws RequestNotValidException, NotFoundException, GenericException, AlreadyExistsException,
    AuthorizationDeniedException, ValidationException, IOException, LockingException {

    AIPState state = AIPState.INGEST_PROCESSING;
    Permissions permissions = new Permissions();
    boolean notify = false;

    String aipType = getType(sip);

    AIP aip = model.createAIP(state, parentId.orElse(null), aipType, permissions, ingestSIPUUID, ingestSIPIds,
      ingestJobId, notify, username);

    PluginHelper.acquireObjectLock(aip, plugin);

    // process IP information
    processIPInformation(model, sip, aip.getId(), notify, false);

    // process IPRepresentation information
    for (IPRepresentation representation : sip.getRepresentations()) {
      processIPRepresentationInformation(model, representation, aip.getId(), notify, false, username, null);
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

  public static AIP earkSIPToAIPUpdate(SIP sip, IndexedAIP indexedAIP, ModelService model, StorageService storage,
    String username, String ingestJobId, Report reportItem, Plugin<?> plugin)
    throws RequestNotValidException, NotFoundException, GenericException, AlreadyExistsException,
    AuthorizationDeniedException, ValidationException, LockingException {
    return earkSIPToAIPUpdate(sip, indexedAIP, model, username, Optional.empty(), ingestJobId, reportItem, plugin);
  }

  public static AIP earkSIPToAIPUpdate(SIP sip, IndexedAIP indexedAIP, ModelService model, String username,
    Optional<String> searchScope, String ingestJobId, Report reportItem, Plugin<?> plugin)
    throws RequestNotValidException, NotFoundException, GenericException, AlreadyExistsException,
    AuthorizationDeniedException, ValidationException, LockingException {
    boolean notify = false;
    AIP aip;

    PluginHelper.acquireObjectLock(indexedAIP, plugin);

    // type UPDATE
    if (reportItem != null) {
      reportItem.getSipInformation().setUpdate(true);
    }

    // process IP information
    processIPInformation(model, sip, indexedAIP.getId(), notify, true);

    // process IPRepresentation information
    for (IPRepresentation representation : sip.getRepresentations()) {
      processIPRepresentationInformation(model, representation, indexedAIP.getId(), notify, true, username, reportItem);
    }

    aip = model.retrieveAIP(indexedAIP.getId());
    aip.setGhost(false);
    if (searchScope.isPresent()) {
      aip.setParentId(searchScope.get());
    }
    aip.addIngestUpdateJobId(ingestJobId);

    for (String id : sip.getIds()) {
      if (!aip.getIngestSIPIds().contains(id)) {
        aip.getIngestSIPIds().add(id);
      }
    }

    return model.updateAIP(aip, username);
  }

  protected static void processIArxiuIPInformation(ModelService model, SIP sip, String aipId, boolean notify)
          throws RequestNotValidException, GenericException, AlreadyExistsException, AuthorizationDeniedException,
          NotFoundException, ValidationException {
    // process descriptive metadata; TODO pending for iArxiu DC schema validation (declaration of element 'oai:dc')
    /*processDescriptiveMetadata(model, aipId, null, sip.getDescriptiveMetadata(), notify, false);*/

    // process other metadata
    processOtherMetadata(model, sip.getOtherMetadata(), aipId, Optional.empty(), notify);

    // process preservation metadata... Not existing for iArxiu! to delete
    /*processPreservationMetadata(model, sip.getPreservationMetadata(), aipId, Optional.empty(), notify);*/

    // process documentation... Not existing for iArxiu! to delete
    /*processDocumentation(model, sip.getDocumentation(), aipId, null, false);*/

    // process schemas
    /*processSchemas(model, sip.getSchemas(), aipId, null, false);*/
  }
  protected static void processIPInformation(ModelService model, SIP sip, String aipId, boolean notify, boolean update)
    throws RequestNotValidException, GenericException, AlreadyExistsException, AuthorizationDeniedException,
    NotFoundException, ValidationException {
    // process descriptive metadata
    processDescriptiveMetadata(model, aipId, null, sip.getDescriptiveMetadata(), notify, update);

    // process other metadata
    processOtherMetadata(model, sip.getOtherMetadata(), aipId, Optional.empty(), notify);

    // process preservation metadata
    processPreservationMetadata(model, sip.getPreservationMetadata(), aipId, Optional.empty(), notify);

    // process documentation
    processDocumentation(model, sip.getDocumentation(), aipId, null, update);

    // process schemas
    processSchemas(model, sip.getSchemas(), aipId, null, update);
  }

  private static void processDescriptiveMetadata(ModelService model, String aipId, String representationId,
    List<IPDescriptiveMetadata> descriptiveMetadata, boolean notify, boolean update) throws RequestNotValidException,
    GenericException, AlreadyExistsException, AuthorizationDeniedException, NotFoundException, ValidationException {
    for (IPDescriptiveMetadata dm : descriptiveMetadata) {
      final String descriptiveMetadataBinary = StringUtils.isNotBlank(dm.getMetadata().getFileName()) ? dm.getMetadata().getFileName() :
              dm.getId(); // uses the descriptive metadata file name for the descriptive Metadata Binary if existing, otherwise the metadata id
      ContentPayload payload = new FSPathContentPayload(dm.getMetadata().getPath());
      String metadataType = getMetadataType(dm);
      String metadataVersion = dm.getMetadataVersion();
      try {
        model.createDescriptiveMetadata(aipId, representationId, descriptiveMetadataBinary, payload, metadataType,
          metadataVersion, notify);
      } catch (AlreadyExistsException e) {
        if (update) {
          final Map<String, String> properties = new HashMap<>();
          properties.put(RodaConstants.VERSION_ACTION, RodaConstants.VersionAction.UPDATE_FROM_SIP.toString());

          model.updateDescriptiveMetadata(aipId, descriptiveMetadataBinary, payload, metadataType, metadataVersion,
            properties);
        } else {
          throw e;
        }
      }
      LOGGER.info("{} AIP '{}' (representation {}) DescriptiveMetadata type {} with binary: {}", update ? "Update" : "Create", aipId, representationId != null ? "'" + representationId + "'" : "none",
              metadataType, descriptiveMetadataBinary);
    }
  }

  private static void processOtherMetadata(ModelService model, List<IPMetadata> otherMetadata, String aipId,
    Optional<String> representationId, boolean notify) throws GenericException, NotFoundException,
    RequestNotValidException, AuthorizationDeniedException {

    for (IPMetadata pm : otherMetadata) {
      IPFile file = pm.getMetadata();
      ContentPayload fileContentPayload = new FSPathContentPayload(file.getPath());

      LOGGER.info("Create or Update AIP '{}' (representation {}) OtherMetadata type {} with file: {}", aipId, representationId.map(id -> "'" + id + "'").orElse("none"),
              pm.getMetadataType(), file);
      model.createOrUpdateOtherMetadata(aipId, representationId.orElse(null), file.getRelativeFolders(),
        file.getFileName(), "", pm.getMetadataType().asString(), fileContentPayload, notify);
    }
  }

  private static void processPreservationMetadata(ModelService model, List<IPMetadata> preservationMetadata,
    String aipId, Optional<String> representationId, boolean notify) throws GenericException, NotFoundException,
    RequestNotValidException, AuthorizationDeniedException, AlreadyExistsException {
    for (IPMetadata pm : preservationMetadata) {
      IPFile file = pm.getMetadata();
      ContentPayload fileContentPayload = new FSPathContentPayload(file.getPath());

      if (representationId.isPresent()) {
        model.createPreservationMetadata(PreservationMetadataType.OTHER, aipId, representationId.get(),
          file.getRelativeFolders(), file.getFileName(), fileContentPayload, notify);
      } else {
        model.createPreservationMetadata(PreservationMetadataType.OTHER, aipId, file.getRelativeFolders(),
          file.getFileName(), fileContentPayload, notify);
      }
    }
  }

  private static void processDocumentation(ModelService model, List<IPFile> documentation, String aipId,
    String representationId, boolean update) throws RequestNotValidException, GenericException, AlreadyExistsException,
    AuthorizationDeniedException, NotFoundException {
    for (IPFile doc : documentation) {
      List<String> directoryPath = doc.getRelativeFolders();
      String fileId = doc.getFileName();
      ContentPayload payload = new FSPathContentPayload(doc.getPath());
      try {
        model.createDocumentation(aipId, representationId, directoryPath, fileId, payload);
        /* TODO for iArxiu SIP documentation fails on: NoSuchFileException '/documentation/../BIN_1/index.xml'
          /var/folders/jj/5r4m2qkx0sz6qv89cblrnkkm0000gn/T/_IArxiuToAIPPluginTest8353180541796317950/data/storage/aip/75d1ece9-64a4-48f4-8e87-d4fd40ce7f8c/documentation/../BIN_1/index.xml
        */
      }
      catch (AlreadyExistsException e) {
        // Tolerate duplicate documentation when updating
        if (!update)
          throw e;
      }
    }
  }

  private static void processSchemas(ModelService model, List<IPFile> schemas, String aipId, String representationId,
    boolean update) throws RequestNotValidException, GenericException, AlreadyExistsException,
    AuthorizationDeniedException, NotFoundException {
    for (IPFile schema : schemas) {
      List<String> directoryPath = schema.getRelativeFolders();
      String fileId = schema.getFileName();
      ContentPayload payload = new FSPathContentPayload(schema.getPath());

      try {
        model.createSchema(aipId, representationId, directoryPath, fileId, payload);
      } catch (AlreadyExistsException e) {
        // Tolerate duplicate schemas when updating
        if (!update)
          throw e;
      }
    }
  }

  public static void processIPRepresentationInformation(ModelService model, IPRepresentation sr, String aipId,
                                                        boolean notify, boolean update, String username, Report reportItem) throws RequestNotValidException,
    GenericException, AlreadyExistsException, AuthorizationDeniedException, NotFoundException, ValidationException {
    String representationType = getType(sr);
    boolean isOriginal = RepresentationStatus.getORIGINAL().equals(sr.getStatus());

    Representation representation = null;
    if (update) {
      try {
        representation = model.retrieveRepresentation(aipId, sr.getObjectID());
      } catch (NotFoundException e) {
        // do nothing
      }
    }
    // Either we're not updating or the retrieve failed
    if (representation == null) {
      representation = model.createRepresentation(aipId, sr.getObjectID(), isOriginal, representationType, notify,
        username);
      if (reportItem != null && update) {
        reportItem.getSipInformation().addRepresentationData(aipId, IdUtils.getRepresentationId(representation));
      }
    }

    // process representation descriptive metadata
    processDescriptiveMetadata(model, aipId, representation.getId(), sr.getDescriptiveMetadata(), notify, update);

    // process other metadata
    processOtherMetadata(model, sr.getOtherMetadata(), aipId, Optional.ofNullable(representation.getId()), notify);

    // process representation preservation metadata
    processPreservationMetadata(model, sr.getPreservationMetadata(), aipId, Optional.ofNullable(representation.getId()),
      notify);

    // process representation files
    processRepresentationFiles(model, aipId, sr, representation, update, notify, reportItem);

    // process representation documentation
    processDocumentation(model, sr.getDocumentation(), aipId, representation.getId(), false);

    // process representation schemas
    processSchemas(model, sr.getSchemas(), aipId, representation.getId(), false);
  }

  public static void processIArxiuIPRepresentationInformation(ModelService model, IPRepresentation sr, String aipId,
                                                        boolean notify, String username) throws RequestNotValidException,
          GenericException, AlreadyExistsException, AuthorizationDeniedException, NotFoundException, ValidationException {
    String representationType = getType(sr);
    boolean isOriginal = RepresentationStatus.getORIGINAL().equals(sr.getStatus());

    final Representation representation = model.createRepresentation(aipId, sr.getObjectID(), isOriginal, representationType, notify,
              username);
    final String representationId = representation.getId();
    LOGGER.info("Created AIP '{}' representation '{}' Information type {}: {}", aipId, representationId, representationType, representation);

    // process representation descriptive metadata
    processDescriptiveMetadata(model, aipId, representationId, sr.getDescriptiveMetadata(), notify, false);

    // process other metadata
    processOtherMetadata(model, sr.getOtherMetadata(), aipId, Optional.ofNullable(representationId), notify);

    // process representation preservation metadata; Not a PreservationMetadata in iArxiu
    /* processPreservationMetadata(model, sr.getPreservationMetadata(), aipId, Optional.ofNullable(representation.getId()),
            notify);*/

    // process representation files
    processRepresentationFiles(model, aipId, sr, representation, false, notify, null);

    // process representation documentation
    /*processDocumentation(model, sr.getDocumentation(), aipId, representation.getId(), false);*/

    // process representation schemas
    /*processSchemas(model, sr.getSchemas(), aipId, representation.getId(), false);*/
  }

  private static void processRepresentationFiles(ModelService model, String aipId, IPRepresentation sr, Representation representation, boolean update, boolean notify, Report reportItem) throws AuthorizationDeniedException, RequestNotValidException, NotFoundException, GenericException, AlreadyExistsException {
    for (IPFile file : sr.getData()) {
      List<String> directoryPath = file.getRelativeFolders();
      String fileId = file.getFileName();
      ContentPayload payload = new FSPathContentPayload(file.getPath());
      final String representationId = representation.getId();
      try {
        final File createdFile = model.createFile(aipId, representationId, directoryPath, fileId, payload, notify);
        if (reportItem != null && update) {
          reportItem.getSipInformation().addFileData(aipId, IdUtils.getRepresentationId(representation), createdFile);
        }
      } catch (AlreadyExistsException e) {
        if (update) {
          final File updatedFile = model.updateFile(aipId, representation.getId(), directoryPath, fileId, payload, true,
                  notify);
          if (reportItem != null) {
            reportItem.getSipInformation().addFileData(aipId, IdUtils.getRepresentationId(representation), updatedFile);
          }
        } else {
          throw e;
        }
      }
      LOGGER.info("{} AIP '{}' representation '{}' file: {}", update ? "Update" : "Create", aipId, representationId, file);
    }
  }

  protected static String getType(SIP sip) {
    return sip.getContentType().asString();
  }

  private static String getType(IPRepresentation sr) {
    return sr.getContentType().asString();
  }

  private static String getMetadataType(IPDescriptiveMetadata dm) {
    MetadataType metadataType = dm.getMetadataType();
    String type = "";
    if (metadataType != null) {
      if (metadataType.getType() == MetadataTypeEnum.OTHER && StringUtils.isNotBlank(metadataType.getOtherType())) {
        type = metadataType.getOtherType();
      } else {
        type = metadataType.getType().getType();
      }
    }
    return type;
  }
}

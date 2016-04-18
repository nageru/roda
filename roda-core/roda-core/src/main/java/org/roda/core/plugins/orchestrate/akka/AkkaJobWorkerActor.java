/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.plugins.orchestrate.akka;

import java.util.ArrayList;
import java.util.List;

import org.roda.core.RodaCoreFactory;
import org.roda.core.data.adapter.sublist.Sublist;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.v2.index.SelectedItems;
import org.roda.core.data.v2.index.SelectedItemsFilter;
import org.roda.core.data.v2.index.SelectedItemsList;
import org.roda.core.data.v2.ip.AIP;
import org.roda.core.data.v2.ip.IndexedAIP;
import org.roda.core.data.v2.ip.TransferredResource;
import org.roda.core.data.v2.jobs.Job;
import org.roda.core.data.v2.jobs.Job.ORCHESTRATOR_METHOD;
import org.roda.core.data.v2.jobs.Report;
import org.roda.core.index.IndexService;
import org.roda.core.plugins.Plugin;
import org.roda.core.plugins.plugins.PluginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class AkkaJobWorkerActor extends UntypedActor {
  private static final Logger LOGGER = LoggerFactory.getLogger(AkkaJobWorkerActor.class);

  public AkkaJobWorkerActor() {

  }

  @Override
  public void onReceive(Object msg) throws Exception {
    if (msg instanceof Job) {
      List<Report> reports = new ArrayList<>();
      Job job = (Job) msg;
      Plugin<?> plugin = (Plugin<?>) RodaCoreFactory.getPluginManager().getPlugin(job.getPlugin());
      PluginHelper.setPluginParameters(plugin, job);

      PluginHelper.updateJobPercentage(plugin, 0);

      if (ORCHESTRATOR_METHOD.ON_TRANSFERRED_RESOURCES == job.getOrchestratorMethod()) {
        if (job.getObjects() instanceof SelectedItemsFilter) {
          SelectedItemsFilter selectedItems = (SelectedItemsFilter) job.getObjects();

          Long objectsCount = RodaCoreFactory.getIndexService().count(TransferredResource.class,
            selectedItems.getFilter());
          PluginHelper.updateJobObjectsCount(plugin, RodaCoreFactory.getModelService(), objectsCount);

          reports = RodaCoreFactory.getPluginOrchestrator().runPluginFromIndex(TransferredResource.class,
            selectedItems.getFilter(), (Plugin<TransferredResource>) plugin);
        } else {
          reports = RodaCoreFactory.getPluginOrchestrator().runPluginOnTransferredResources(
            (Plugin<TransferredResource>) plugin, getTransferredResourcesFromObjectIds(job.getObjects()));
        }
        // FIXME 20160404 hsilva: this should be done inside the orchestrator
        // method
        PluginHelper.updateJobPercentage(plugin, 100);
      } else if (ORCHESTRATOR_METHOD.ON_ALL_AIPS == job.getOrchestratorMethod()) {
        reports = RodaCoreFactory.getPluginOrchestrator().runPluginOnAllAIPs((Plugin<AIP>) plugin);
        // FIXME 20160404 hsilva: this should be done inside the orchestrator
        // method
        PluginHelper.updateJobPercentage(plugin, 100);
      } else if (ORCHESTRATOR_METHOD.ON_AIPS == job.getOrchestratorMethod()) {
        reports = RodaCoreFactory.getPluginOrchestrator().runPluginOnAIPs((Plugin<AIP>) plugin,
          getAIPs(job.getObjects()));
        // FIXME 20160404 hsilva: this should be done inside the orchestrator
        // method
        PluginHelper.updateJobPercentage(plugin, 100);
      } else if (ORCHESTRATOR_METHOD.RUN_PLUGIN == job.getOrchestratorMethod()) {
        RodaCoreFactory.getPluginOrchestrator().runPlugin(plugin);
      }

      getSender().tell(reports, getSelf());
    }
  }

  public List<TransferredResource> getTransferredResourcesFromObjectIds(SelectedItems selectedItems) {
    List<TransferredResource> res = new ArrayList<TransferredResource>();
    if (selectedItems instanceof SelectedItemsList) {
      SelectedItemsList list = (SelectedItemsList) selectedItems;
      for (String objectId : list.getIds()) {
        try {
          res.add(RodaCoreFactory.getIndexService().retrieve(TransferredResource.class, objectId));
        } catch (GenericException | NotFoundException e) {
          LOGGER.error("Error retrieving TransferredResource", e);
        }
      }
    } else {
      LOGGER.error("Still not implemented!!!!!!!!");
    }
    return res;
  }

  public List<String> getAIPs(SelectedItems selectedItems) {
    List<String> res = new ArrayList<String>();
    if (selectedItems instanceof SelectedItemsList) {
      SelectedItemsList list = (SelectedItemsList) selectedItems;
      res.addAll(list.getIds());
    } else {
      try {
        IndexService index = RodaCoreFactory.getIndexService();
        SelectedItemsFilter selectedItemsFilter = (SelectedItemsFilter) selectedItems;
        long count = index.count(IndexedAIP.class, selectedItemsFilter.getFilter());
        for (int i = 0; i < count; i += RodaConstants.DEFAULT_PAGINATION_VALUE) {
          List<IndexedAIP> aips = index.find(IndexedAIP.class, selectedItemsFilter.getFilter(), null,
            new Sublist(i, RodaConstants.DEFAULT_PAGINATION_VALUE), null).getResults();
          for (IndexedAIP aip : aips) {
            res.add(aip.getId());
          }
        }
      } catch (Throwable e) {
        LOGGER.error("Error while retrieving AIPs from index", e);
      }
    }
    return res;
  }

}
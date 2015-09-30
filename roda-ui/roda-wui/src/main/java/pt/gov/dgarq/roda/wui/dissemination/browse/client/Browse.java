/**
 * 
 */
package pt.gov.dgarq.roda.wui.dissemination.browse.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

import config.i18n.client.BrowseMessages;
import pt.gov.dgarq.roda.core.common.Pair;
import pt.gov.dgarq.roda.core.common.RodaConstants;
import pt.gov.dgarq.roda.core.data.adapter.filter.EmptyKeyFilterParameter;
import pt.gov.dgarq.roda.core.data.adapter.filter.Filter;
import pt.gov.dgarq.roda.core.data.adapter.filter.SimpleFilterParameter;
import pt.gov.dgarq.roda.core.data.v2.IndexResult;
import pt.gov.dgarq.roda.core.data.v2.Representation;
import pt.gov.dgarq.roda.core.data.v2.RepresentationState;
import pt.gov.dgarq.roda.core.data.v2.RodaUser;
import pt.gov.dgarq.roda.core.data.v2.SimpleDescriptionObject;
import pt.gov.dgarq.roda.wui.common.client.ClientLogger;
import pt.gov.dgarq.roda.wui.common.client.HistoryResolver;
import pt.gov.dgarq.roda.wui.common.client.UserLogin;
import pt.gov.dgarq.roda.wui.common.client.tools.DescriptionLevelUtils;
import pt.gov.dgarq.roda.wui.common.client.tools.RestErrorOverlayType;
import pt.gov.dgarq.roda.wui.common.client.tools.RestUtils;
import pt.gov.dgarq.roda.wui.common.client.tools.Tools;
import pt.gov.dgarq.roda.wui.common.client.widgets.AIPList;
import pt.gov.dgarq.roda.wui.common.client.widgets.MessagePopup;
import pt.gov.dgarq.roda.wui.main.client.BreadcrumbItem;
import pt.gov.dgarq.roda.wui.main.client.BreadcrumbPanel;

/**
 * @author Luis Faria
 * 
 */
public class Browse extends Composite {

  private static final String TOP_ICON = "<i class='fa fa-circle-o'></i>";

  public static final HistoryResolver RESOLVER = new HistoryResolver() {

    @Override
    public void resolve(List<String> historyTokens, AsyncCallback<Widget> callback) {
      getInstance().resolve(historyTokens, callback);
    }

    @Override
    public void isCurrentUserPermitted(AsyncCallback<Boolean> callback) {
      UserLogin.getInstance().checkRole(this, callback);
    }

    @Override
    public String getHistoryToken() {
      return "browse";
    }

    @Override
    public List<String> getHistoryPath() {
      return Arrays.asList(getHistoryToken());
    }
  };

  public static final List<String> getViewItemHistoryToken(String id) {
    return Tools.concat(RESOLVER.getHistoryPath(), id);
  }

  interface MyUiBinder extends UiBinder<Widget, Browse> {
  }

  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  private static Browse instance = null;

  /**
   * Get the singleton instance
   * 
   * @return the instance
   */
  public static Browse getInstance() {
    if (instance == null) {
      instance = new Browse();
    }
    return instance;
  }

  // private static CommonConstants constants = (CommonConstants)
  // GWT.create(CommonConstants.class);

  private static Filter COLLECTIONS_FILTER = new Filter(new EmptyKeyFilterParameter(RodaConstants.AIP_PARENT_ID));

  // private static BrowseConstants constants = (BrowseConstants)
  // GWT.create(BrowseConstants.class);

  private static BrowseMessages messages = (BrowseMessages) GWT.create(BrowseMessages.class);
  //
  // private static BrowseImageBundle browseImageBundle = (BrowseImageBundle)
  // GWT.create(BrowseImageBundle.class);

  private ClientLogger logger = new ClientLogger(getClass().getName());

  // private SimplePanel viewPanelContainer;

  private String aipId;

  @UiField(provided = true)
  BreadcrumbPanel breadcrumb;

  @UiField
  SimplePanel itemIcon;

  @UiField
  Label itemTitle;

  @UiField
  Label itemDates;

  @UiField
  TabPanel itemMetadata;

  @UiField
  Label fondsPanelTitle;

  @UiField(provided = true)
  AIPList fondsPanel;

  @UiField
  FlowPanel sidebarData;

  @UiField
  FlowPanel downloadList;

  @UiField
  Button createItem;

  @UiField
  Button createDescriptiveMetadata;

  @UiField
  Button moveItem;

  @UiField
  Button editPermissions;

  @UiField
  Button remove;

  private boolean viewingTop;

  private Browse() {
    viewingTop = true;
    breadcrumb = new BreadcrumbPanel();
    fondsPanel = new AIPList();
    initWidget(uiBinder.createAndBindUi(this));

    fondsPanel.getSelectionModel().addSelectionChangeHandler(new Handler() {

      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        SimpleDescriptionObject sdo = fondsPanel.getSelectionModel().getSelectedObject();
        if (sdo != null) {
          view(sdo.getId());
        }
      }
    });

    fondsPanel.addValueChangeHandler(new ValueChangeHandler<IndexResult<SimpleDescriptionObject>>() {

      @Override
      public void onValueChange(ValueChangeEvent<IndexResult<SimpleDescriptionObject>> event) {
        fondsPanelTitle.setVisible(!viewingTop && event.getValue().getTotalCount() > 0);
        fondsPanel.setVisible(event.getValue().getTotalCount() > 0);
      }
    });
  }

  protected void onPermissionsUpdate(RodaUser user) {
    if (user.hasRole(RodaConstants.REPOSITORY_PERMISSIONS_METADATA_EDITOR)) {
      createItem.setVisible(true);
      // refresh.setVisible(true);
    } else {
      createItem.setVisible(false);
      // refresh.setVisible(false);
    }
  }

  public void resolve(List<String> historyTokens, AsyncCallback<Widget> callback) {
    if (historyTokens.size() == 0) {
      viewAction();
      callback.onSuccess(this);
    } else if (historyTokens.size() == 1) {
      viewAction(historyTokens.get(0));
      callback.onSuccess(this);
    } else
      if (historyTokens.size() > 1 && historyTokens.get(0).equals(EditDescriptiveMetadata.RESOLVER.getHistoryToken())) {
      EditDescriptiveMetadata.RESOLVER.resolve(Tools.tail(historyTokens), callback);
    } else if (historyTokens.size() > 1
      && historyTokens.get(0).equals(CreateDescriptiveMetadata.RESOLVER.getHistoryToken())) {
      CreateDescriptiveMetadata.RESOLVER.resolve(Tools.tail(historyTokens), callback);
    } else {
      Tools.newHistory(RESOLVER);
      callback.onSuccess(null);
    }
  }

  /**
   * Call the view action by the history token
   * 
   * @param id
   *          the pid of the object to view. if pid is null, then the base state
   *          will be called
   */
  public void view(final String id) {
    boolean historyUpdated = updateHistory(id);

    if (!historyUpdated) {
      viewAction(id);
    }
  }

  protected void viewAction(final String id) {
    if (id == null) {
      viewAction();
    } else {
      aipId = id;
      BrowserService.Util.getInstance().getItemBundle(id, LocaleInfo.getCurrentLocale().getLocaleName(),
        new AsyncCallback<BrowseItemBundle>() {

          @Override
          public void onFailure(Throwable caught) {
            logger.error("Could not view id=" + id, caught);
            showError(id, caught);
          }

          @Override
          public void onSuccess(BrowseItemBundle itemBundle) {
            viewAction(itemBundle);
          }
        });
    }
  }

  protected void showError(String id, Throwable caught) {
    breadcrumb.updatePath(new ArrayList<BreadcrumbItem>());
    breadcrumb.setVisible(false);

    HTMLPanel itemIconHtmlPanel = DescriptionLevelUtils.getElementLevelIconHTMLPanel(null);
    itemIconHtmlPanel.addStyleName("browseItemIcon-other");
    itemIcon.setWidget(itemIconHtmlPanel);
    itemTitle.setText(id);
    itemDates.setText("");

    itemMetadata.clear();
    itemMetadata.add(new HTML(SafeHtmlUtils.fromSafeConstant(caught.getMessage())));
    itemMetadata.setVisible(true);

    viewingTop = false;
    fondsPanelTitle.setVisible(false);
    fondsPanel.setVisible(false);

    downloadList.clear();
    sidebarData.setVisible(false);

    // Set button visibility
    createItem.setVisible(false);
    createDescriptiveMetadata.setVisible(false);
    moveItem.setVisible(false);
    editPermissions.setVisible(false);
    remove.setVisible(true);
  }

  protected void viewAction(BrowseItemBundle itemBundle) {
    if (itemBundle != null) {
      SimpleDescriptionObject sdo = itemBundle.getSdo();
      List<DescriptiveMetadataViewBundle> descMetadata = itemBundle.getDescriptiveMetadata();
      PreservationMetadataBundle preservationMetadata = itemBundle.getPreservationMetadata();
      List<Representation> representations = itemBundle.getRepresentations();

      breadcrumb.updatePath(getBreadcrumbsFromAncestors(itemBundle.getSdoAncestors(), sdo));
      breadcrumb.setVisible(true);
      HTMLPanel itemIconHtmlPanel = DescriptionLevelUtils.getElementLevelIconHTMLPanel(sdo.getLevel());
      itemIconHtmlPanel.addStyleName("browseItemIcon-other");
      itemIcon.setWidget(itemIconHtmlPanel);
      itemTitle.setText(sdo.getTitle() != null ? sdo.getTitle() : sdo.getId());
      itemTitle.removeStyleName("browseTitle-allCollections");
      itemDates.setText(getDatesText(sdo));

      itemMetadata.clear();
      final List<Pair<String, HTML>> descriptiveMetadataContainers = new ArrayList<Pair<String, HTML>>();
      for (DescriptiveMetadataViewBundle bundle : descMetadata) {
        String title = bundle.getLabel();
        HTML container = new HTML();
        container.addStyleName("metadataContent");
        itemMetadata.add(container, title);
        descriptiveMetadataContainers.add(Pair.create(bundle.getId(), container));
      }

      itemMetadata.addSelectionHandler(new SelectionHandler<Integer>() {

        @Override
        public void onSelection(SelectionEvent<Integer> event) {
          if (event.getSelectedItem() < descriptiveMetadataContainers.size()) {
            Pair<String, HTML> pair = descriptiveMetadataContainers.get(event.getSelectedItem());
            String descId = pair.getFirst();
            final HTML html = pair.getSecond();
            if (html.getText().length() == 0) {
              getDescriptiveMetadataHTML(descId, new AsyncCallback<SafeHtml>() {

                @Override
                public void onFailure(Throwable caught) {
                  MessagePopup.showError("Error loading descriptive metadata");
                }

                @Override
                public void onSuccess(SafeHtml result) {
                  html.setHTML(result);
                }
              });
            }
          }
        }

      });

      if (!preservationMetadata.getRepresentationsMetadata().isEmpty()) {
        final HTML premisContainer = new HTML();
        final int premisTabIndex = itemMetadata.getWidgetCount();
        itemMetadata.add(premisContainer, "PREMIS");
        itemMetadata.addSelectionHandler(new SelectionHandler<Integer>() {

          @Override
          public void onSelection(SelectionEvent<Integer> event) {
            if (event.getSelectedItem() == premisTabIndex && premisContainer.getText().length() == 0) {
              // TODO load PREMIS via REST API
              // getPreservationMetadataHTML(new AsyncCallback<SafeHtml>() {
              //
              // @Override
              // public void onFailure(Throwable caught) {
              // premisContainer.setHTML(SafeHtmlUtils.fromString(caught.getMessage()));
              // }
              //
              // @Override
              // public void onSuccess(SafeHtml result) {
              // premisContainer.setHTML(result);
              // JavascriptUtils.runHighlighter();
              // JavascriptUtils.slideToggle(".toggle-next");
              // JavascriptUtils.smoothScroll();
              // }
              // });
            }
          }
        });
        premisContainer.addStyleName("preservationMetadata");
        premisContainer.addStyleName("metadataContent");
      }

      if (!descMetadata.isEmpty() || !preservationMetadata.getRepresentationsMetadata().isEmpty()) {
        itemMetadata.setVisible(true);
        itemMetadata.selectTab(0);
      } else {
        itemMetadata.setVisible(false);
      }

      viewingTop = false;
      fondsPanelTitle.setVisible(true);
      Filter filter = new Filter(new SimpleFilterParameter(RodaConstants.AIP_PARENT_ID, sdo.getId()));
      fondsPanel.setFilter(filter);

      downloadList.clear();
      sidebarData.setVisible(representations.size() > 0);

      for (Representation rep : representations) {
        downloadList.add(createRepresentationDownloadPanel(rep));
      }

      // Set button visibility
      createItem.setVisible(true);
      createDescriptiveMetadata.setVisible(true);
      moveItem.setVisible(true);
      editPermissions.setVisible(true);
      remove.setVisible(true);

    } else {
      viewAction();
    }
  }

  protected void viewAction() {
    aipId = null;
    HTMLPanel topIcon = new HTMLPanel(SafeHtmlUtils.fromSafeConstant(TOP_ICON));
    topIcon.addStyleName("browseItemIcon-all");
    itemIcon.setWidget(topIcon);

    breadcrumb.updatePath(
      Arrays.asList(new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(TOP_ICON), RESOLVER.getHistoryPath())));
    breadcrumb.setVisible(false);
    itemTitle.setText("All collections");
    itemTitle.addStyleName("browseTitle-allCollections");
    itemDates.setText("");
    itemMetadata.clear();
    itemMetadata.setVisible(false);
    viewingTop = true;
    fondsPanelTitle.setVisible(false);
    fondsPanel.setFilter(COLLECTIONS_FILTER);

    sidebarData.setVisible(false);
    downloadList.clear();

    // Set button visibility
    createItem.setVisible(true);
    createDescriptiveMetadata.setVisible(false);
    moveItem.setVisible(false);
    editPermissions.setVisible(false);
    remove.setVisible(false);
  }

  private List<BreadcrumbItem> getBreadcrumbsFromAncestors(List<SimpleDescriptionObject> sdoAncestors,
    SimpleDescriptionObject sdo) {
    List<BreadcrumbItem> ret = new ArrayList<>();
    ret.add(new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(TOP_ICON), RESOLVER.getHistoryPath()));
    for (SimpleDescriptionObject ancestor : sdoAncestors) {
      SafeHtml breadcrumbLabel = getBreadcrumbLabel(ancestor);
      BreadcrumbItem ancestorBreadcrumb = new BreadcrumbItem(breadcrumbLabel,
        getViewItemHistoryToken(ancestor.getId()));
      ret.add(1, ancestorBreadcrumb);
    }

    ret.add(new BreadcrumbItem(getBreadcrumbLabel(sdo), getViewItemHistoryToken(sdo.getId())));
    return ret;
  }

  private SafeHtml getBreadcrumbLabel(SimpleDescriptionObject ancestor) {
    SafeHtml elementLevelIconSafeHtml = DescriptionLevelUtils.getElementLevelIconSafeHtml(ancestor.getLevel());
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    String label = ancestor.getTitle() != null ? ancestor.getTitle() : ancestor.getId();
    builder.append(elementLevelIconSafeHtml).append(SafeHtmlUtils.fromString(label));
    SafeHtml breadcrumbLabel = builder.toSafeHtml();
    return breadcrumbLabel;
  }

  private Widget createRepresentationDownloadPanel(Representation rep) {
    FlowPanel downloadPanel = new FlowPanel();
    HTML icon = new HTML(SafeHtmlUtils.fromSafeConstant("<i class='fa fa-download'></i>"));

    SafeHtml labelText;
    Set<RepresentationState> statuses = rep.getStatuses();
    if (statuses.containsAll(Arrays.asList(RepresentationState.ORIGINAL, RepresentationState.NORMALIZED))) {
      labelText = SafeHtmlUtils.fromString("Original and normalized document");
    } else if (statuses.contains(RepresentationState.ORIGINAL)) {
      labelText = SafeHtmlUtils.fromString("Original document");
    } else if (statuses.contains(RepresentationState.NORMALIZED)) {
      labelText = SafeHtmlUtils.fromString("Normalized document");
    } else {
      labelText = SafeHtmlUtils.fromString("Document");
    }

    FlowPanel labelsPanel = new FlowPanel();

    Anchor label = new Anchor(labelText, RestUtils.createRepresentationDownloadUri(rep.getAipId(), rep.getId()));
    Label subLabel = new Label(rep.getFileIds().size() + " files, " + readableFileSize(rep.getSizeInBytes()));

    labelsPanel.add(label);
    labelsPanel.add(subLabel);
    downloadPanel.add(icon);
    downloadPanel.add(labelsPanel);

    downloadPanel.addStyleName("browseDownload");
    icon.addStyleName("browseDownloadIcon");
    labelsPanel.addStyleName("browseDownloadLabels");
    label.addStyleName("browseDownloadLabel");
    subLabel.addStyleName("browseDownloadSublabel");
    return downloadPanel;
  }

  /**
   * TODO move this to Utils
   * 
   * @param size
   * @return
   */
  public static String readableFileSize(long size) {
    if (size <= 0)
      return "0 B";
    final String[] units = new String[] {"B", "KB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return NumberFormat.getFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
  }

  private void getDescriptiveMetadataHTML(final String descId, final AsyncCallback<SafeHtml> callback) {
    String uri = RestUtils.createDescriptiveMetadataHTMLUri(aipId, descId);
    RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, uri);
    try {
      requestBuilder.sendRequest(null, new RequestCallback() {

        @Override
        public void onResponseReceived(Request request, Response response) {
          if (200 == response.getStatusCode()) {
            String html = response.getText();

            SafeHtmlBuilder b = new SafeHtmlBuilder();
            // Download link
            SafeUri downloadUri = RestUtils.createDescriptiveMetadataDownloadUri(aipId, descId);
            String downloadLinkHtml = "<a href='" + downloadUri.asString()
              + "' class='descriptiveMetadataLink'>download</a>";
            b.append(SafeHtmlUtils.fromSafeConstant(downloadLinkHtml));

            // Edit link
            String editLink = Tools.createHistoryHashLink(EditDescriptiveMetadata.RESOLVER, aipId, descId);
            String editLinkHtml = "<a href='" + editLink + "' class='descriptiveMetadataLink'>edit</a>";
            b.append(SafeHtmlUtils.fromSafeConstant(editLinkHtml));

            b.append(SafeHtmlUtils.fromTrustedString(html));
            SafeHtml safeHtml = b.toSafeHtml();

            callback.onSuccess(safeHtml);
          } else {
            String text = response.getText();
            String message;
            try {
              RestErrorOverlayType error = (RestErrorOverlayType) JsonUtils.safeEval(text);
              message = error.getMessage();
            } catch (IllegalArgumentException e) {
              message = text;
            }

            SafeHtmlBuilder b = new SafeHtmlBuilder();

            // Edit link
            String editLink = Tools.createHistoryHashLink(EditDescriptiveMetadata.RESOLVER, aipId, descId);
            String editLinkHtml = "<a href='" + editLink + "' class='descriptiveMetadataLink'>edit</a>";
            b.append(SafeHtmlUtils.fromSafeConstant(editLinkHtml));

            // error message
            b.append(SafeHtmlUtils.fromSafeConstant("<span class='error'>"));
            b.append(messages.descriptiveMetadataTranformToHTMLError());
            b.append(SafeHtmlUtils.fromSafeConstant("<pre><code>"));
            b.append(SafeHtmlUtils.fromString(message));
            b.append(SafeHtmlUtils.fromSafeConstant("</core></pre>"));
            b.append(SafeHtmlUtils.fromSafeConstant("</span>"));

            callback.onSuccess(b.toSafeHtml());
          }
        }

        @Override
        public void onError(Request request, Throwable exception) {
          callback.onFailure(exception);
        }
      });
    } catch (RequestException e) {
      callback.onFailure(e);
    }

  }

  private String getDatesText(SimpleDescriptionObject sdo) {
    String ret;
    DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

    Date dateInitial = sdo.getDateInitial();
    Date dateFinal = sdo.getDateFinal();

    if (dateInitial == null && dateFinal == null) {
      ret = "";
    } else if (dateInitial != null && dateFinal == null) {
      ret = "From " + formatter.format(sdo.getDateInitial());
    } else if (dateInitial == null && dateFinal != null) {
      ret = "Up to " + formatter.format(sdo.getDateFinal());
    } else {
      ret = formatter.format(sdo.getDateInitial()) + " to " + formatter.format(sdo.getDateFinal());
    }

    return ret;
  }

  private boolean updateHistory(String id) {
    boolean historyUpdated;
    List<String> path;
    if (id == null) {
      path = RESOLVER.getHistoryPath();
    } else {
      path = getViewItemHistoryToken(id);
    }

    if (path.equals(History.getToken())) {
      historyUpdated = false;
    } else {
      logger.debug("calling new history token");
      Tools.newHistory(path);
      historyUpdated = true;
    }
    return historyUpdated;
  }

  @UiHandler("createItem")
  void buttonCreateItemHandler(ClickEvent e) {
    BrowserService.Util.getInstance().createAIP(aipId, new AsyncCallback<String>() {

      @Override
      public void onFailure(Throwable caught) {
        MessagePopup.showError("Error creating item");
      }

      @Override
      public void onSuccess(String itemAIPId) {
        view(itemAIPId);
      }
    });
  }

  @UiHandler("createDescriptiveMetadata")
  void buttonCreateDescriptiveMetadataHandler(ClickEvent e) {
    if (aipId != null) {
      Tools.newHistory(RESOLVER, CreateDescriptiveMetadata.RESOLVER.getHistoryToken(), aipId);
    }
  }

  @UiHandler("remove")
  void buttonRemoveHandler(ClickEvent e) {
    if (aipId != null) {
      BrowserService.Util.getInstance().removeAIP(aipId, new AsyncCallback<Void>() {

        @Override
        public void onFailure(Throwable caught) {
          MessagePopup.showError("Error deleteing item");
        }

        @Override
        public void onSuccess(Void result) {
          Tools.newHistory(RESOLVER);
        }
      });
    }
  }

}

/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It
	   is required that all modified versions of this material be marked in
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program
	   with regards to rights under trademark law for use of the trade names
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.asset.web.action.assetmaster;

import static java.lang.Boolean.FALSE;
import static org.egov.asset.util.AssetConstants.CREATEASSET;
import static org.egov.asset.util.AssetConstants.MODIFYASSET;
import static org.egov.asset.util.AssetConstants.VIEWASSET;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.egov.asset.model.Asset;
import org.egov.asset.model.AssetActivities;
import org.egov.asset.model.AssetCategory;
import org.egov.asset.model.AssetType;
import org.egov.asset.service.AppService;
import org.egov.asset.service.AssetActivitiesService;
import org.egov.asset.service.AssetService;
import org.egov.asset.util.AssetConstants;
import org.egov.asset.util.AssetIdentifier;
import org.egov.commons.EgwStatus;
import org.egov.exceptions.EGOVException;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.BoundaryType;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.entity.HierarchyType;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.config.AppConfigValues;
import org.egov.infstr.search.SearchQuery;
import org.egov.infstr.search.SearchQueryHQL;
import org.egov.lib.admbndry.BoundaryDAO;
import org.egov.lib.admbndry.BoundaryTypeDAO;
import org.egov.lib.admbndry.HeirarchyTypeDAO;
import org.egov.web.actions.SearchFormAction;
import org.egov.web.annotation.ValidationErrorPage;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

@ParentPackage("egov")
@Result(name=Action.SUCCESS, type="ServletRedirectResult.class", location="asset.action")
public class AssetAction extends SearchFormAction{
	public static final  String SEARCH="search";
	public static final  String SEARCH_PLUGIN="searchplugin";
	public static final  String CREATE_PLUGIN="create";
	public static final  String VIEW="view";
	
	private static final Logger LOGGER = Logger.getLogger(AssetAction.class);
	private AssetService assetService;
	private AssetActivitiesService assetActivitiesService;
	private AppService appService;
	private Asset asset = new Asset();
	private List<Asset> assetList = null;
	private Long id;
	private static final String LOCATION_HIERARCHY_TYPE = "LOCATION";
	private static final String ADMIN_HIERARCHY_TYPE = "ADMINISTRATION";
	
	private static final String AREA_BOUNDARY_TYPE = "Area";
	private static final String LOACTION_BOUNDARY_TYPE = "Locality";
	private static final String WARD_BOUNDARY_TYPE = "Ward";
	private static final String Zone_BOUNDARY_TYPE = "Zone";
	private static final String Asset_SAVE_SUCCESS ="asset.save.success";
	private static final String WardList="wardList";
	private static final String StatusList="statusList";
	private static final String Unable_To_Load_Heirarchy_Information="Unable to load Heirarchy information";
	private static final String Error_While_Loading_HeirarchyType="Error while loading HeirarchyType - HeirarchyType.";
	// UI fields
	private String userMode;
	private boolean fDisabled;
	private boolean sDisabled;
	private String dataDisplayStyle;
	private Integer rowId;
	
	//asset search page
	private Long parentId;
	private Long catTypeId;
	private Integer departmentId;	
	private List<Integer> statusId;
	private List<String> assetStatus;
	//selectedstatusId
	private String code;
	private String description;
	private Long locationId; 
	private Long zoneId; 
	private Long areaId;
	private Long streetId;
	private Long street2Id;
	private Long wardId;

	private String selectType;
	private String xmlconfigname;
	private String isAutoGeneratedCode;
	
	private String categoryname;
	private String category;
	private String searchBy;

	private String messageKey;
	private List<Long> assetChildCategoryList= new LinkedList<Long>();
	Query query=null;
	private BigDecimal lengthValue;
	private BigDecimal widthValue;
	private BigDecimal areaValue;
	
	private Long parentCategoryId;
	private List<Long> subCategoryIds;
	private Boolean fromDiaryModule = FALSE;
	private String actionType;
	
	@Autowired
	private HeirarchyTypeDAO heirarchyTypeDAO;
	@Autowired
	private BoundaryDAO boundaryDAO;
	@Autowired
        private BoundaryTypeDAO boundaryTypeDAO;
	
	
	/**
	 * Default Constructor
	 */
	public AssetAction(){
		addRelatedEntity("assetType", AssetType.class);
		addRelatedEntity("department", Department.class);
		addRelatedEntity("assetCategory", AssetCategory.class);
		addRelatedEntity("area", Boundary.class);
		addRelatedEntity("location", Boundary.class);
		addRelatedEntity("street", Boundary.class);
		addRelatedEntity("street2", Boundary.class);
		addRelatedEntity("ward", Boundary.class);
		addRelatedEntity("zone", Boundary.class);
		addRelatedEntity("status", EgwStatus.class,"description");
	}
	
	@Override
	public void prepare() {
		isAutoGeneratedCode = appService.getUniqueAppConfigValue("IS_ASSET_CODE_AUTOGENERATED");
		if (id != null && id != -1) {
			 asset = assetService.findById(id, false);
			 AssetActivities activities = assetActivitiesService.find(" from AssetActivities where asset.id=?",asset.getId());
			 	if(activities!=null)
			 		asset.setGrossValue(activities.getAdditionAmount());
		 		//asset.setAccDepreciation(assetService.getDepreciationAmt(asset));
	    }
		super.prepare();
		setupDropdownDataExcluding("area","location","street","street2","ward","zone","status");	
		
		// Fetch HeirarchyType
		HierarchyType hType = null;
		try{	
			hType = heirarchyTypeDAO.getHierarchyTypeByName(LOCATION_HIERARCHY_TYPE);
		}catch(EGOVException e){
			LOGGER.error(Error_While_Loading_HeirarchyType + e.getMessage());
			addFieldError("Heirarchy", Unable_To_Load_Heirarchy_Information);
			throw new EGOVRuntimeException(Unable_To_Load_Heirarchy_Information,e);
		}
		
		/**
		 * Fetch Area Dropdown List
		 */
		List<Boundary> areaList =new ArrayList<Boundary>();
		if(asset.getArea()!=null || areaId!=null){
			BoundaryType bType = boundaryTypeDAO.getBoundaryType(AREA_BOUNDARY_TYPE, hType);
			areaList = boundaryDAO.getAllBoundariesInclgHxByBndryTypeId(bType.getId());
		}
		addDropdownData("areaList", areaList);
		addDropdownData("parentCatList", persistenceService.findAllBy("from AssetCategory where parent is null"));
		AjaxAssetAction ajaxAssetAction  = new AjaxAssetAction();
		ajaxAssetAction.setPersistenceService(getPersistenceService());
		populateSubCategories(ajaxAssetAction);
		
		 /**
		 *  Fetch Location Dropdown List
		 */
		List<Boundary> locationList = new ArrayList<Boundary>();
		try{
			if(asset.getArea()!=null){
				locationList = boundaryDAO.getChildBoundariesInclgHx(asset.getArea().getId());
			}
			if(areaId!=null){
				locationList = boundaryDAO.getChildBoundariesInclgHx(areaId);
			}
		}catch(Exception e){
			LOGGER.error("Error while loading location - location." + e.getMessage());
		}
		addDropdownData("locationList", locationList);

		 /**
		 *  Fetch Ward Dropdown List
		 */
		if(zoneId==null){
			List<Boundary> wardList = new ArrayList<Boundary>();
			addDropdownData(WardList, wardList);
		}else{
			List<Boundary> wardList = null;
			try {
				wardList = boundaryDAO.getChildBoundariesInclgHx(zoneId);
			} catch (Exception e){
				LOGGER.error("Error while loading wards - wards." + e.getMessage());
		}
		addDropdownData(WardList,wardList);
		}
		
		
		 /**
		 *  Fetch Ward Dropdown List
		 */
		 addDropdownData("wardsList",getAllWard());
		
		
		/**
		 *  Fetch Street Dropdown List
		 */
		 List<Boundary> streetList = new ArrayList<Boundary>();
		 if(wardId!=null){
			 BoundaryType childBoundaryType = boundaryTypeDAO.getBoundaryType("Street", hType);
			 Boundary parentBoundary = boundaryDAO.getBoundaryInclgHxById(wardId);
			 streetList = new LinkedList(boundaryDAO.getCrossHeirarchyChildren(parentBoundary, childBoundaryType));
		 }
		 if(asset.getWard()!=null){
			 BoundaryType childBoundaryType = boundaryTypeDAO.getBoundaryType("Street", hType);
			 Boundary parentBoundary = boundaryDAO.getBoundaryInclgHxById(asset.getWard().getId());
			 streetList = new LinkedList(boundaryDAO.getCrossHeirarchyChildren(parentBoundary, childBoundaryType));
		 }
		 addDropdownData("streetList", streetList);
		 
		/**
		 *  Fetch Street Dropdown List
		 */
		 if(locationId==null){
			 List<Boundary> street2List = new ArrayList<Boundary>();
			 addDropdownData("street2List", street2List);
		 }else{
			 List<Boundary> street2List = null;
			 try {
				 street2List = boundaryDAO.getChildBoundariesInclgHx(locationId);	
			 } catch (Exception e){
				 LOGGER.error("Error while loading wards - wards." + e.getMessage());
			 }
			 addDropdownData("street2List", street2List);
		}
		
		/**
		 *   Fetch Acquisition Mode Dropdown List
		 */
		List<AppConfigValues> configList = appService.getAppConfigValue("Assets", "MODE_OF_ACQUISITION");
		addDropdownData("acquisitionModeList", configList);
		
		/**
		 *  Fetch Status Dropdown List
		 */
		String query = "from EgwStatus st where st.moduletype='ASSET' order by description";
		List<EgwStatus> status  = (List<EgwStatus>) persistenceService.findAllBy(query);
		addDropdownData(StatusList, status);
		
		/**
		 *  Fetch Zone Dropdown List
		 */
		addDropdownData("zoneList", getAllZone());
		if(searchBy==null)
			searchBy="1";
	}
	
	private void populateSubCategories(AjaxAssetAction ajaxAssetAction){
		if(parentCategoryId!=null && parentCategoryId!=-1){
			ajaxAssetAction.setParentCatId(parentCategoryId);
			ajaxAssetAction.populateSubCategories();
			addDropdownData("subCategoriesList",ajaxAssetAction.getAssetSubCategoryList());
		}
		else {
			addDropdownData("subCategoriesList", Collections.emptyList());
		}
	}
	
	/**
	 * This method is invoked to create a new form.
	 * 
	 * @return a <code>String</code> representing the value 'NEW'
	 */
	public String newform(){ 
		userMode = NEW;
		return showform();  
	}
	
	public String showform()
	{  
		LOGGER.info("****User Mode: " + userMode);
		String result=null;
		
		if(userMode==null)
		{
			userMode=NEW;
		}
		if(NEW.equals(userMode))
		{
			fDisabled = false;
			sDisabled = false;
			result = NEW;
		}else if(VIEW.equals(userMode))
		{
			if(id == null){
				addActionError(getText("asset.category.id.null"));			
				result = SEARCH;
			}
			else{
				fDisabled = true;
				sDisabled = true;
				setLocationDetails(asset);
				result = NEW;
			}
		}else if(EDIT.equals(userMode))
		{
			if(id == null){
				addActionError(getText("asset.id.null"));			
				result = SEARCH;
			}
			else{
				fDisabled = false;
				sDisabled = false;
				setCategory(category);
				setLocationDetails(asset);
				result = NEW;
			}
		}
			
		return result;  
	}
   	private void setLocationDetails(Asset asset){
   		
   		
		if(asset.getArea()!=null){
			List<Boundary> locationList = new LinkedList<Boundary>();
			try{	
				locationList = boundaryDAO.getChildBoundaries(asset.getArea().getId());
			}catch(Exception e){
				LOGGER.error("Error while loading locations - locations." + e.getMessage());
				addFieldError("location", "Unable to load location information");
				throw new EGOVRuntimeException("Unable to load location information",e);
			}
			addDropdownData("locationList", locationList);
		}
		if(asset.getWard()!=null){
			AjaxAssetAction ajaxAssetAction=new AjaxAssetAction();
			ajaxAssetAction.setWardId(asset.getWard().getId());
			try{
				ajaxAssetAction.populateStreets();
				addDropdownData("streetList",ajaxAssetAction.getStreetList());
			}
			catch(Exception e){
				LOGGER.error("Error while loading Streets." + e.getMessage());
				addFieldError("streets", "Unable to load Streets Information");
				throw new EGOVRuntimeException("Unable to load Streets information",e);
			}
		}
		if(asset.getWard()!=null){
			setZoneId(asset.getWard().getParent().getId());
			List<Boundary> wardList = null;
			try {
				wardList = boundaryDAO.getChildBoundaries(String.valueOf(zoneId));
			} catch (Exception e){
				LOGGER.error("Error while loading wards - wards." + e.getMessage());
			}
			addDropdownData(WardList,wardList);
		}
	}
	
	public List<Boundary> getAllLocation() {
	        HierarchyType hType = null;
		try{	
			hType = heirarchyTypeDAO.getHierarchyTypeByName(LOCATION_HIERARCHY_TYPE);
		}catch(EGOVException e){
			LOGGER.error(Error_While_Loading_HeirarchyType + e.getMessage());
			throw new EGOVRuntimeException(Unable_To_Load_Heirarchy_Information,e);
		}
		List<Boundary> locationList = null;
		BoundaryType bType = boundaryTypeDAO.getBoundaryType(LOACTION_BOUNDARY_TYPE, hType);
		locationList = boundaryDAO.getAllBoundariesByBndryTypeId(bType.getId());
		return locationList;
	}

	public List<Boundary> getAllWard() {
	        HierarchyType hType = null;
		try{	
			hType = heirarchyTypeDAO.getHierarchyTypeByName(ADMIN_HIERARCHY_TYPE);
		}catch(EGOVException e){
			LOGGER.error(Error_While_Loading_HeirarchyType + e.getMessage());
			throw new EGOVRuntimeException(Unable_To_Load_Heirarchy_Information,e);
		}
		List<Boundary> wardList = null;
		BoundaryType bType = boundaryTypeDAO.getBoundaryType(WARD_BOUNDARY_TYPE, hType);
		wardList = boundaryDAO.getAllBoundariesInclgHxByBndryTypeId(bType.getId());
		return wardList;
	}
	
	public List<Boundary> getAllZone() {
	    HierarchyType hType = null;
		try{	
			hType = heirarchyTypeDAO.getHierarchyTypeByName(ADMIN_HIERARCHY_TYPE);
		}catch(EGOVException e){
			LOGGER.error(Error_While_Loading_HeirarchyType+ e.getMessage());
			throw new EGOVRuntimeException(Unable_To_Load_Heirarchy_Information,e);
		}
		List<Boundary> zoneList = null;
		BoundaryType bType = boundaryTypeDAO.getBoundaryType(Zone_BOUNDARY_TYPE,hType);
		if(actionType == null){
			actionType = "";
		}
		if(actionType.equalsIgnoreCase(CREATEASSET)){
			zoneList = boundaryDAO.getAllBoundariesByBndryTypeId(bType.getId());
		} else if(actionType == "" || (actionType.equalsIgnoreCase(VIEWASSET) || actionType.equalsIgnoreCase(MODIFYASSET))){
			if("edit".equalsIgnoreCase(userMode))
				zoneList = boundaryDAO.getAllBoundariesByBndryTypeId(bType.getId());
			else
				zoneList = boundaryDAO.getAllBoundariesInclgHxByBndryTypeId(bType.getId());
		}
		return zoneList;
	}
	
	public String edit()
	{  
		userMode = EDIT;
		return SEARCH;  
	} 
	
	public String view()
	{  
		userMode = VIEW;
		return SEARCH;
	} 
	
	/**
	 * Search Page for Assets view and edit screen
	 * @throws Exception 
	 */
	public String list() throws Exception 
	{  
		setXmlconfigname(xmlconfigname);
	    setCategoryname(categoryname);
	    setCatTypeId(catTypeId);
		if(departmentId==null 
				&& locationId==null
				&& catTypeId == null
				&& (code==null || code.trim().equalsIgnoreCase("")) 
				&& (description==null || description.trim().equalsIgnoreCase("")) 
				&& (statusId==null || statusId.isEmpty()) && zoneId==-1){		
			messageKey = "message.mandatory";
			addActionError(getText(messageKey, "At least one selection is required"));			
			return SEARCH;
		}
		setPageSize(AssetConstants.PAGE_SIZE);
		search();
		return SEARCH;  
	}
	
	private List<Asset> searchAssets() throws Exception{
		HashMap<String, Object> queryAndParam = getQueryAndParam();
		List<Object> params= (List<Object>)queryAndParam.get("param");
		
		Object parameterObj[]=new Object[params.size()];
		for(int element=0;element<params.size();element++){
			parameterObj[element]=params.get(element);
			
		}
		// System.out.println("parameterObj----->"+parameterObj);    
		//return new SearchQueryHQL(queryAndParam.get("query").toString(), "select count(*) " + queryAndParam.get("query"),(List<Object>)queryAndParam.get("param"));
		return assetService.findAllBy(queryAndParam.get("query").toString(), parameterObj);
	}
	
	private List getAllChilds(){
	    return persistenceService.findAllByNamedQuery("ParentChildCategories", parentId);
	}
	/**
	 * Method to setup request parameter received from other modules
	 */
	private void setupRequestData(){
		getSession().put(StatusList, getStatusList(assetStatus));
		setStatusList();
	}
	
	/**
	 * Get the list of <code>EgwStatus</code> related to ASSET module.
	 * @param statusDescList - List of status descriptions
	 * @return
	 */
	private List<EgwStatus> getStatusList(List<String> statusDescList){
		List<EgwStatus> lStatusList = null;
		if(statusDescList!=null && !statusDescList.isEmpty()){
			StringBuffer sql = new StringBuffer(100);
			sql.append("from EgwStatus st where st.moduletype='ASSET'  and UPPER(st.description) in (");
			//sql.append(" and UPPER(st.description) in ("); 
			for(int i=0,len=statusDescList.size(); i<len;i++){
				 sql.append("'" + statusDescList.get(i).trim().toUpperCase() + "'");
				 if(i<len-1){
					 sql.append(',');
				 }
			 }			 
			 sql.append(") order by description");
			 String query = sql.toString();
			 lStatusList  = (List<EgwStatus>) persistenceService.findAllBy(query);
		}
		return lStatusList;
	}
	
	private void setStatusList(){
		List<EgwStatus> statusList = (List<EgwStatus>)getSession().get("statusList");
		if(statusList==null){
			statusList = new LinkedList<EgwStatus>();
		}
		addDropdownData(StatusList, statusList);
	}
	
	/**
	 * asset search plugin for other modules - works and stores
	 */
	public String showSearchPage(){
		setupRequestData();
		return SEARCH_PLUGIN;
	}
	
	
	public String showSerachResult() throws Exception{
		setStatusList();
		if(statusId!=null && !statusId.isEmpty())	
			assetList = searchAssets();
		else
			addFieldError("status", "Please select at least one status");

		return SEARCH_PLUGIN;
	}
	
	/**
	 * test page for search plugin - not in use
	 */
	public String showPlugin(){
		return "plugin";
	}
	
	/**
	 * asset create plugin for other modules - works and stores
	 */
	public String showCreatePage(){
		setupRequestData();
		return CREATE_PLUGIN;
	}
	
	/**
	 * create asset from other modules
	 */
	@ValidationErrorPage(value="create")
	public String create(){
		try {
			validateForDimensions();
			setStatusList();
			assetService.setAssetNumber(asset);
			assetService.persist(asset);
			addActionMessage( "\'" + asset.getCode() + "\' " + getText(Asset_SAVE_SUCCESS));
			id = asset.getId();
			// make to view mode
			fDisabled = true;
			sDisabled = true;
			setLocationDetails(asset);
			return CREATE_PLUGIN;
		} catch (ValidationException e) {
			 clearMessages();
			 prepare();
			 List<ValidationError> errors=new ArrayList<ValidationError>();
			 errors.add(new ValidationError("exp",e.getErrors().get(0).getMessage()));
			 throw new ValidationException(errors);
		} 
		
	}
	
	private void validateForDimensions()
	{
		String categoryName =  appService.getUniqueAppConfigValue("PARENT_CATEGORY_DIMENSIONS_VALIDATION");
		if(categoryName!=null && asset.getAssetCategory()!=null && asset.getAssetCategory().getId()!=null)
		{
			String innerQuery = "(select ac2.id from AssetCategory ac2 where  ac2.name=?  )";
			List<AssetCategory> categList = persistenceService.findAllBy(" from AssetCategory  ac1 where ac1.id=? and (ac1.id in "+innerQuery+" or ac1.parent in " +innerQuery+")",
					asset.getAssetCategory().getId(),categoryName,categoryName);
			if(categList!=null && categList.size()>0 && (lengthValue==null || widthValue==null || areaValue==null))
			{
				throw new ValidationException(Arrays.asList(new ValidationError("asset.dimension.mandatory", "asset.dimension.mandatory")));
			}
			if(categList==null || categList.isEmpty() &&  (lengthValue!=null || widthValue!=null || areaValue!=null))
			{
				throw new ValidationException(Arrays.asList(new ValidationError("asset.dimension.not.applicable", "asset.dimension.not.applicable")));
			}
		}
		asset.setLength(lengthValue);
		asset.setWidth(widthValue);
		asset.setTotalArea(areaValue);
	}
	
	public String save(){
		validateForDimensions();
		if(asset.getDateOfCreation()!=null)
			assetService.setAssetNumber(asset);
		assetService.persist(asset);
		
		if(NEW.equals(userMode) && asset.getStatus().getDescription().equalsIgnoreCase("Capitalized"))
		{
			AssetActivities activities = new AssetActivities();
			activities.setAsset(asset);
			activities.setActivityDate(asset.getDateOfCreation());
			activities.setIdentifier(AssetIdentifier.C);
			activities.setAdditionAmount(asset.getGrossValue());
			assetActivitiesService.persist(activities);			
		}
		addActionMessage( "\'" + asset.getCode() + "\' " + getText(Asset_SAVE_SUCCESS));
		userMode = EDIT;
		id = asset.getId();
		setLocationDetails(asset);
		return showform();
	}

	/**
	 * The default action method
	 */
	public String execute() { 
		return view();
	}
	
	
	// Property accessors
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Object getModel() {
		return asset;
	}

	public void setModel(Asset asset){
		this.asset = asset;
	}
	
	// Spring Injection
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}
	
	// Spring Injection
	public void setAppService(AppService appService) {
		this.appService = appService;
	}

	public String getUserMode() {
		return userMode;
	}

	public void setUserMode(String userMode) {
		this.userMode = userMode;
	}

	public boolean isFDisabled() {
		return fDisabled;
	}

	public boolean isSDisabled() {
		return sDisabled;
	}

	public List<Asset> getAssetList() {
		return assetList;
	}

	public void setAssetList(List<Asset> assetList) {
		this.assetList = assetList;
	}

	public String getDataDisplayStyle() {
		return dataDisplayStyle;
	}

	/**
	 * @return the parentId
	 */
	public Long getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the catTypeId
	 */
	public Long getCatTypeId() {
		return catTypeId;
	}

	/**
	 * @param catTypeId the catTypeId to set
	 */
	public void setCatTypeId(Long catTypeId) {
		this.catTypeId = catTypeId;
	}

	/**
	 * @return the departmentId
	 */
	public Integer getDepartmentId() {
		return departmentId;
	}

	/**
	 * @param departmentId the departmentId to set
	 */
	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the locationId
	 */
	public Long getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId the wardId to set for search
	 */
	public void setLocationId(Long wardId) {
		this.locationId = wardId;
	}

	/**
	 * @return the messageKey
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * @param messageKey the messageKey to set
	 */
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * @return the statusId
	 */
	public List<Integer> getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(List<Integer> statusId) {
		this.statusId = statusId;
	}

	public void setAssetStatus(List<String> assetStatus) {
		this.assetStatus = assetStatus;
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public List<Long> getAssetChildCategoryList() {
		return assetChildCategoryList;
	}

	
	public String getSelectType() {
		return selectType;
	}

	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}
	public String getXmlconfigname() {
		return xmlconfigname;
	}

	public void setXmlconfigname(String xmlconfigname) {
		this.xmlconfigname = xmlconfigname;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIsAutoGeneratedCode() {
		return isAutoGeneratedCode;
	}

	public void setIsAutoGeneratedCode(String isAutoGeneratedCode) {
		this.isAutoGeneratedCode = isAutoGeneratedCode;
	}

	@Override
	public SearchQuery prepareQuery(String sortField, String sortOrder) {
		Map<String, Object> queryAndParam = getQueryAndParam();		
		//System.out.println("getQueryAndParam()"+getQueryAndParam());
		return new SearchQueryHQL(queryAndParam.get("query").toString(), "select count(*) " + queryAndParam.get("query"),(List<Object>)queryAndParam.get("param"));
	}

	/**
	 * @return query in string format
	 */
	private HashMap<String, Object> getQueryAndParam() {
		StringBuilder sql = new StringBuilder(265);
		List<Object> parameters = new ArrayList<Object>();
		int counter = 0;
		if(userMode!=null && VIEW.equalsIgnoreCase(userMode)) {
			sql.append("from Asset asset where asset.code is not null ");
		}
		else {
			sql.append("from Asset asset where asset.code is not null and asset.status.code<>'CANCELLED' ");
		}
		if(catTypeId!=null && catTypeId!=-1){
			sql.append(" and asset.assetCategory.assetType.id = ?");
			// System.out.println("assetcatId---> "+catTypeId);
			parameters.add(catTypeId);
			counter++;
		}
		if(departmentId!=null && departmentId!=-1){
			sql.append(" and asset.department.id = ?");	
		// System.out.println("deptid--->" +departmentId);
			parameters.add(departmentId);
		counter++;
		}
		if(zoneId!=null && zoneId !=-1 ){
			sql.append(" and asset.ward.parent.id = ?");
			// System.out.println("zoneid--->"+zoneId);
			parameters.add(zoneId);
			counter++;
		}
		if(wardId!=null && wardId!=-1){
			sql.append(" and asset.ward.id = ?");
		//	System.out.println("wardId-->" +wardId);
			parameters.add(wardId);
			counter++;
			}
		if(streetId!=null && streetId!=-1){
			sql.append(" and asset.street.id = ?");
		//	System.out.println("streetId-->"+streetId);
			parameters.add(streetId);
			counter++;
			}

		if(areaId!=null && areaId!=-1){
			sql.append(" and asset.area.id = ?");
		//	System.out.println("areaId-->" +areaId);
			parameters.add(areaId);
			counter++;
			}
		if(locationId!=null && locationId!=-1){
			sql.append(" and asset.location.id = ?");
		//	System.out.println("locationId--->" +locationId);
			parameters.add(locationId); 
			counter++;
		}
		if(street2Id!=null && street2Id!=-1){
			sql.append(" and asset.street.id = ?");
		//	System.out.println("street2Id--->" +street2Id);
			parameters.add(street2Id);
			counter++;
		}
		if(code!=null && !code.trim().equalsIgnoreCase("")){
			sql.append(" and UPPER(asset.code) like ?");
		//	System.out.println("code--->"+code);
			parameters.add("%"+code.toUpperCase()+"%");
			counter++;
		}
		if(description!=null && !description.trim().equalsIgnoreCase("")){
			sql.append(" and UPPER(asset.description) like ?");
		//	System.out.println("desc-->"+description);
			parameters.add("%"+description.toUpperCase()+"%");
			counter++;
		}

		if(statusId!=null && !statusId.isEmpty()){
			String statusList="";
			for(Integer status:statusId){
				statusList=statusList+status+",";
				 }
			sql.append(" and asset.status.id in ("+statusList.substring(0,statusList.length()-1)+")"); 
			 }		
		
		if (parentId != null && parentId != -1 && !parentId.equals("")) {
			sql.append(" and asset.assetCategory.id = ? "); 
			parameters.add(parentId);			
		}
		
		if(parentCategoryId!=null && parentCategoryId!=-1 && (subCategoryIds==null || (subCategoryIds.size()==1 && subCategoryIds.get(0)==-1))){
			sql.append(" and asset.assetCategory.parent.id = ? "); 
			parameters.add(parentCategoryId);			
		}
		else if(parentCategoryId!=null && parentCategoryId!=-1 && subCategoryIds!=null && !subCategoryIds.isEmpty()){
				String subCatList="";
				for(Long subCat : subCategoryIds)
					subCatList=subCatList+subCat+",";
				sql.append(" and asset.assetCategory.id in ("+subCatList.substring(0,subCatList.length()-1)+")");
		}
		
				
		HashMap<String, Object> queryAndParam = new HashMap<String, Object>();
		queryAndParam.put("query", sql);
		queryAndParam.put("param", parameters);
		return queryAndParam;
	}
	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public Long getStreetId() {
		return streetId;
	}

	public void setStreetId(Long streetId) {
		this.streetId = streetId;
	}

	public Long getStreet2Id() {
		return street2Id;
	}

	public void setStreet2Id(Long street2Id) {
		this.street2Id = street2Id;
	}

	public Long getWardId() {
		return wardId;
	}

	public void setWardId(Long wardId) {
		this.wardId = wardId;
	}
	public String getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	public void setAssetActivitiesService(AssetActivitiesService assetActivitiesService) {
		this.assetActivitiesService = assetActivitiesService;
	}

	public BigDecimal getLengthValue() {
		return lengthValue;
	}

	public BigDecimal getWidthValue() {
		return widthValue;
	}

	public BigDecimal getAreaValue() {
		return areaValue;
	}

	public void setLengthValue(BigDecimal lengthValue) {
		this.lengthValue = lengthValue;
	}

	public void setWidthValue(BigDecimal widthValue) {
		this.widthValue = widthValue;
	}

	public void setAreaValue(BigDecimal areaValue) {
		this.areaValue = areaValue;
	}

	public Long getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public List<Long> getSubCategoryIds() {
		return subCategoryIds;
	}

	public void setSubCategoryIds(List<Long> subCategoryIds) {
		this.subCategoryIds = subCategoryIds;
	}

	public Boolean getFromDiaryModule() {
		return fromDiaryModule;
	}

	public void setFromDiaryModule(Boolean fromDiaryModule) {
		this.fromDiaryModule = fromDiaryModule;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
		
}


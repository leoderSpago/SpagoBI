/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/
package it.eng.spagobi.tools.importexport;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.ObjTemplate;
import it.eng.spagobi.analiticalmodel.document.bo.Snapshot;
import it.eng.spagobi.analiticalmodel.document.bo.SubObject;
import it.eng.spagobi.analiticalmodel.document.dao.IBIObjectDAO;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjFunc;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjFuncId;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjPar;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjTemplates;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiObjects;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSnapshots;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubObjects;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubreports;
import it.eng.spagobi.analiticalmodel.document.metadata.SbiSubreportsId;
import it.eng.spagobi.analiticalmodel.functionalitytree.bo.LowFunctionality;
import it.eng.spagobi.analiticalmodel.functionalitytree.dao.ILowFunctionalityDAO;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFuncRole;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFuncRoleId;
import it.eng.spagobi.analiticalmodel.functionalitytree.metadata.SbiFunctions;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.Parameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ParameterUse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.dao.IObjParuseDAO;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiObjParuseId;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParameters;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuse;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseCk;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseCkId;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseDet;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata.SbiParuseDetId;
import it.eng.spagobi.behaviouralmodel.check.bo.Check;
import it.eng.spagobi.behaviouralmodel.check.metadata.SbiChecks;
import it.eng.spagobi.behaviouralmodel.lov.bo.ModalitiesValue;
import it.eng.spagobi.behaviouralmodel.lov.metadata.SbiLov;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.bo.Role;
import it.eng.spagobi.commons.bo.Subreport;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IBinContentDAO;
import it.eng.spagobi.commons.dao.IDomainDAO;
import it.eng.spagobi.commons.metadata.SbiBinContents;
import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.engines.config.metadata.SbiEngines;
import it.eng.spagobi.kpi.alarm.bo.Alarm;
import it.eng.spagobi.kpi.alarm.bo.AlarmContact;
import it.eng.spagobi.kpi.alarm.dao.ISbiAlarmDAO;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarm;
import it.eng.spagobi.kpi.alarm.metadata.SbiAlarmContact;
import it.eng.spagobi.kpi.config.bo.Kpi;
import it.eng.spagobi.kpi.config.bo.KpiDocuments;
import it.eng.spagobi.kpi.config.bo.KpiInstPeriod;
import it.eng.spagobi.kpi.config.bo.KpiInstance;
import it.eng.spagobi.kpi.config.bo.MeasureUnit;
import it.eng.spagobi.kpi.config.bo.Periodicity;
import it.eng.spagobi.kpi.config.dao.IKpiDAO;
import it.eng.spagobi.kpi.config.dao.IKpiInstPeriodDAO;
import it.eng.spagobi.kpi.config.dao.IKpiInstanceDAO;
import it.eng.spagobi.kpi.config.dao.IMeasureUnitDAO;
import it.eng.spagobi.kpi.config.dao.IPeriodicityDAO;
import it.eng.spagobi.kpi.config.metadata.SbiKpi;
import it.eng.spagobi.kpi.config.metadata.SbiKpiDocument;
import it.eng.spagobi.kpi.config.metadata.SbiKpiInstPeriod;
import it.eng.spagobi.kpi.config.metadata.SbiKpiInstance;
import it.eng.spagobi.kpi.config.metadata.SbiKpiPeriodicity;
import it.eng.spagobi.kpi.config.metadata.SbiMeasureUnit;
import it.eng.spagobi.kpi.model.bo.Model;
import it.eng.spagobi.kpi.model.bo.ModelAttribute;
import it.eng.spagobi.kpi.model.bo.ModelAttributeValue;
import it.eng.spagobi.kpi.model.bo.ModelInstance;
import it.eng.spagobi.kpi.model.bo.ModelResources;
import it.eng.spagobi.kpi.model.bo.Resource;
import it.eng.spagobi.kpi.model.dao.IModelDAO;
import it.eng.spagobi.kpi.model.dao.IModelInstanceDAO;
import it.eng.spagobi.kpi.model.dao.IModelResourceDAO;
import it.eng.spagobi.kpi.model.dao.IResourceDAO;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModel;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelAttr;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelAttrVal;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelInst;
import it.eng.spagobi.kpi.model.metadata.SbiKpiModelResources;
import it.eng.spagobi.kpi.model.metadata.SbiResources;
import it.eng.spagobi.kpi.threshold.bo.Threshold;
import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;
import it.eng.spagobi.kpi.threshold.dao.IThresholdDAO;
import it.eng.spagobi.kpi.threshold.metadata.SbiThreshold;
import it.eng.spagobi.kpi.threshold.metadata.SbiThresholdValue;
import it.eng.spagobi.mapcatalogue.bo.GeoFeature;
import it.eng.spagobi.mapcatalogue.bo.GeoMap;
import it.eng.spagobi.mapcatalogue.bo.GeoMapFeature;
import it.eng.spagobi.mapcatalogue.bo.dao.ISbiGeoFeaturesDAO;
import it.eng.spagobi.mapcatalogue.bo.dao.ISbiGeoMapFeaturesDAO;
import it.eng.spagobi.mapcatalogue.bo.dao.ISbiGeoMapsDAO;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoFeatures;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMapFeatures;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMapFeaturesId;
import it.eng.spagobi.mapcatalogue.metadata.SbiGeoMaps;
import it.eng.spagobi.tools.dataset.bo.FileDataSet;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.bo.JavaClassDataSet;
import it.eng.spagobi.tools.dataset.bo.ScriptDataSet;
import it.eng.spagobi.tools.dataset.bo.WebServiceDataSet;
import it.eng.spagobi.tools.dataset.dao.IDataSetDAO;
import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;
import it.eng.spagobi.tools.dataset.metadata.SbiFileDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiJClassDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiQueryDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiScriptDataSet;
import it.eng.spagobi.tools.dataset.metadata.SbiWSDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.metadata.SbiDataSource;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetacontent;
import it.eng.spagobi.tools.objmetadata.bo.ObjMetadata;
import it.eng.spagobi.tools.objmetadata.dao.IObjMetacontentDAO;
import it.eng.spagobi.tools.objmetadata.metadata.SbiObjMetacontents;
import it.eng.spagobi.tools.objmetadata.metadata.SbiObjMetadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

/**
 * Implements methods to insert exported metadata into the exported database 
 */
public class ExporterMetadata {

	static private Logger logger = Logger.getLogger(ExporterMetadata.class);
	private List biObjectToInsert=null;

	// list of ids of models that have an attribute with value filled.
	List modelsWithAttributeValued = null;

	/**
	 * Insert a domain into the exported database.
	 * 
	 * @param domain Domain object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertDomain(Domain domain, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiDomains where valueId = " + domain.getValueId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiDomains hibDomain = new SbiDomains(domain.getValueId());
			hibDomain.setDomainCd(domain.getDomainCode());
			hibDomain.setDomainNm(domain.getDomainName());
			hibDomain.setValueCd(domain.getValueCd());
			hibDomain.setValueDs(domain.getValueDescription());
			hibDomain.setValueNm(domain.getValueName());
			session.save(hibDomain);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting domain into export database ",e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}	
	}


	/**
	 * Insert a Object Metadata Category into the exported database.
	 * 
	 * @param objMetadata ObjMetadata object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertObjMetadata(ObjMetadata objMetadata, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiObjMetadata where objMetaId = " + objMetadata.getObjMetaId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiObjMetadata hibObjMeta = new SbiObjMetadata();
			hibObjMeta.setObjMetaId(objMetadata.getObjMetaId());
			hibObjMeta.setCreationDate(objMetadata.getCreationDate());
			hibObjMeta.setDescription(objMetadata.getDescription());
			hibObjMeta.setLabel(objMetadata.getLabel());
			hibObjMeta.setName(objMetadata.getName());

			if(objMetadata.getDataType() != null){
				SbiDomains dataType=(SbiDomains)session.load(SbiDomains.class, objMetadata.getDataType());
				hibObjMeta.setDataType(dataType);
			}

			session.save(hibObjMeta);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting objMetadata into export database ",e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}	
	}


	/**
	 * Insert a Object Metadata Content into the exported database.
	 * 
	 * @param objMetadataContent ObjMetadataContent object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertObjMetacontent(ObjMetacontent objMetacontent, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiObjMetacontents where objMetacontentId = " + objMetacontent.getObjMetacontentId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			// first of all insert binary content
			if (objMetacontent.getBinaryContentId() != null) {
				// get the content			
				IBinContentDAO binContentDAO = DAOFactory.getBinContentDAO();
				byte[] content = binContentDAO.getBinContent(objMetacontent.getBinaryContentId());
				insertBinContet(objMetacontent.getBinaryContentId(), content, session);
			}

			Transaction tx = session.beginTransaction();

			SbiObjMetacontents hibObjMetacontents = new SbiObjMetacontents();

			hibObjMetacontents.setObjMetacontentId(objMetacontent.getObjMetacontentId());
			hibObjMetacontents.setCreationDate(objMetacontent.getCreationDate());
			hibObjMetacontents.setLastChangeDate(objMetacontent.getLastChangeDate());
			hibObjMetacontents.setObjmetaId(objMetacontent.getObjmetaId());

			// get the object to insert if present
			if (objMetacontent.getBiobjId() != null) {
				SbiObjects sbiObjects = (SbiObjects) session.load(SbiObjects.class, objMetacontent.getBiobjId());
				hibObjMetacontents.setSbiObjects(sbiObjects);
				logger.debug("inserted sbi " + objMetacontent.getBiobjId() + " Object metacontent");
			}
			// get the sub object to insert if present
			if (objMetacontent.getSubobjId() != null) {
				SbiSubObjects sbiSubObjects = (SbiSubObjects) session.load(SbiSubObjects.class, objMetacontent.getSubobjId());
				hibObjMetacontents.setSbiSubObjects(sbiSubObjects);
				logger.debug("inserted sbi " + objMetacontent.getSubobjId() + " SubObject metacontent");
			}
			// get the content
			if (objMetacontent.getBinaryContentId() != null) {
				SbiBinContents sbiBinContents = (SbiBinContents) session.load(SbiBinContents.class, objMetacontent.getBinaryContentId());
				hibObjMetacontents.setSbiBinContents(sbiBinContents);
				// insert the binary content!!				
				logger.debug("inserted sbi " + objMetacontent.getBinaryContentId() + " Binary Content metacontent");
			}

			session.save(hibObjMetacontents);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting objMetadataContent into export database ",e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}	
	}

	/**
	 * Insert data source.
	 * 
	 * @param ds the ds
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertDataSource(IDataSource ds, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiDataSource where dsId = " + ds.getDsId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiDomains dialect=(SbiDomains)session.load(SbiDomains.class, ds.getDialectId());

			SbiDataSource hibDS = new SbiDataSource(ds.getDsId());
			hibDS.setDescr(ds.getDescr());
			hibDS.setDriver(ds.getDriver());
			hibDS.setJndi(ds.getJndi());
			hibDS.setLabel(ds.getLabel());
			hibDS.setPwd(ds.getPwd());
			hibDS.setUrl_connection(ds.getUrlConnection());
			hibDS.setUser(ds.getUser());
			hibDS.setDialect(dialect);
			hibDS.setSchemaAttribute(ds.getSchemaAttribute());
			hibDS.setMultiSchema(ds.getMultiSchema());		

			// va aggiunto il legame con gli engine e il doc ????

			session.save(hibDS);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting dataSource into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert data set.
	 * 
	 * @param dataset the dataset
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertDataSet(IDataSet dataset, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			// if it is a query data set, insert datasource first, before opening a new transaction
			if (dataset instanceof JDBCDataSet) {
				IDataSource ds = ((JDBCDataSet) dataset).getDataSource();
				if (ds != null) insertDataSource(ds, session);
			}

			Transaction tx = session.beginTransaction();

			Query hibQuery = session.createQuery(" from SbiDataSetConfig where dsId = " + dataset.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			SbiDataSetConfig hibDataset = null;
			if (dataset instanceof FileDataSet) {
				hibDataset = new SbiFileDataSet();
				((SbiFileDataSet) hibDataset).setFileName(((FileDataSet) dataset).getFileName()); 
			}
			if (dataset instanceof JDBCDataSet) {
				hibDataset = new SbiQueryDataSet();
				((SbiQueryDataSet) hibDataset).setQuery(((JDBCDataSet) dataset).getQuery().toString());
				// insert the association between the dataset and the datasource
				IDataSource ds = ((JDBCDataSet) dataset).getDataSource();
				if (ds != null) {
					SbiDataSource dsHib = (SbiDataSource) session.load(SbiDataSource.class, new Integer(ds.getDsId()));
					((SbiQueryDataSet) hibDataset).setDataSource(dsHib);
				}
			}
			if (dataset instanceof WebServiceDataSet) {
				hibDataset = new SbiWSDataSet();
				((SbiWSDataSet) hibDataset).setAdress(((WebServiceDataSet) dataset).getAddress());
				((SbiWSDataSet) hibDataset).setOperation(((WebServiceDataSet) dataset).getOperation());
			}
			if (dataset instanceof JavaClassDataSet) {
				hibDataset = new SbiJClassDataSet();
				((SbiJClassDataSet) hibDataset).setJavaClassName(((JavaClassDataSet) dataset).getClassName());
			}
			if (dataset instanceof ScriptDataSet) {
				hibDataset = new SbiScriptDataSet();
				((SbiScriptDataSet) hibDataset).setScript(((ScriptDataSet) dataset).getScript());
				((SbiScriptDataSet) hibDataset).setLanguageScript(((ScriptDataSet) dataset).getLanguageScript());
			}
			hibDataset.setDsId(dataset.getId());
			hibDataset.setLabel(dataset.getLabel());
			hibDataset.setName(dataset.getName());
			hibDataset.setDescription(dataset.getDescription());
			hibDataset.setParameters(dataset.getParameters());
			hibDataset.setPivotColumnName(dataset.getPivotColumnName());
			hibDataset.setPivotColumnValue(dataset.getPivotColumnValue());
			hibDataset.setPivotRowName(dataset.getPivotRowName());
			hibDataset.setNumRows(dataset.isNumRows());
			hibDataset.setDsMetadata(dataset.getDsMetadata());

			if (dataset.getTransformerId() != null) {
				SbiDomains transformerType = (SbiDomains) session.load(SbiDomains.class, dataset.getTransformerId());
				hibDataset.setTransformer(transformerType);
			} else {
				hibDataset.setTransformer(null);
			}

			session.save(hibDataset);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting dataSet into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert an engine into the exported database.
	 * 
	 * @param engine Engine Object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertEngine(Engine engine, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiEngines where engineId = " + engine.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiEngines hibEngine = new SbiEngines(engine.getId());
			hibEngine.setName(engine.getName());
			hibEngine.setLabel(engine.getLabel());
			hibEngine.setDescr(engine.getDescription());
			hibEngine.setDriverNm(engine.getDriverName());
			hibEngine.setEncrypt(new Short((short)engine.getCriptable().intValue()));
			hibEngine.setMainUrl(engine.getUrl());
			hibEngine.setObjUplDir(engine.getDirUpload());
			hibEngine.setObjUseDir(engine.getDirUsable());
			hibEngine.setSecnUrl(engine.getSecondaryUrl());
			SbiDomains objTypeDom = (SbiDomains)session.load(SbiDomains.class, engine.getBiobjTypeId());
			hibEngine.setBiobjType(objTypeDom);
			hibEngine.setClassNm(engine.getClassName());
			SbiDomains engineTypeDom = (SbiDomains)session.load(SbiDomains.class, engine.getEngineTypeId());
			hibEngine.setEngineType(engineTypeDom);
			hibEngine.setUseDataSource(new Boolean(engine.getUseDataSource()));
			if (engine.getUseDataSource() && engine.getDataSourceId() != null) {
				SbiDataSource ds = (SbiDataSource) session.load(SbiDataSource.class, engine.getDataSourceId());
				hibEngine.setDataSource(ds);
			}
			hibEngine.setUseDataSet(new Boolean(engine.getUseDataSet()));
			session.save(hibEngine);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting engine into export database " ,e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert all Snapshot and their binary content.
	 * 
	 * @param biobj the biobj
	 * @param snapshotLis the snapshot lis
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertAllSnapshot(BIObject biobj, List snapshotLis, Session session) throws EMFUserError {
		logger.debug("IN");
		Iterator iter=snapshotLis.iterator();
		while(iter.hasNext()){
			insertSnapshot(biobj,(Snapshot)iter.next(),session);
		}
		logger.debug("OUT");
	}

	/**
	 * Insert a single sub object and their binary content
	 * @param biobj
	 * @param subObject
	 * @param session
	 * @throws EMFUserError
	 */
	private void insertSnapshot(BIObject biobj, Snapshot snapshot, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiSnapshots where snapId = " + snapshot.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				logger.warn("Exist another SbiSnapshot");
				return;
			}

			SbiObjects hibBIObj = new SbiObjects(biobj.getId());

			byte[] template = snapshot.getContent();

			SbiBinContents hibBinContent = new SbiBinContents();
			hibBinContent.setId(snapshot.getBinId());
			hibBinContent.setContent(template);

			SbiSnapshots sub=new SbiSnapshots();
			sub.setCreationDate(snapshot.getDateCreation());
			sub.setDescription(snapshot.getDescription());
			sub.setName(snapshot.getName());
			sub.setSbiBinContents(hibBinContent);
			sub.setSbiObject(hibBIObj);
			sub.setSnapId(snapshot.getId());


			session.save(sub);
			session.save(hibBinContent);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}	

	/**
	 * Insert all SubObject and their binary content.
	 * 
	 * @param biobj the biobj
	 * @param subObjectLis the sub object lis
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertAllSubObject(BIObject biobj, List subObjectLis, Session session) throws EMFUserError {
		logger.debug("IN");
		Iterator iter=subObjectLis.iterator();
		while(iter.hasNext()){
			SubObject subObject = (SubObject)iter.next();
			insertSubObject(biobj,subObject,session);

			//  insert metadata associated to subObject
			logger.debug("search for metadata associate to subobject wit ID "+subObject.getId());
			IObjMetacontentDAO objMetacontentDAO = DAOFactory.getObjMetacontentDAO();
			//  get metacontents associated to object
			List metacontents = objMetacontentDAO.loadObjOrSubObjMetacontents(biobj.getId(), subObject.getId());
			for (Iterator iterator = metacontents.iterator(); iterator.hasNext();) {
				ObjMetacontent metacontent = (ObjMetacontent) iterator.next();
				insertObjMetacontent(metacontent, session);
			}

		}
		logger.debug("OUT");
	}
	/**
	 * Insert a single sub object and their binary content
	 * @param biobj
	 * @param subObject
	 * @param session
	 * @throws EMFUserError
	 */
	private void insertSubObject(BIObject biobj, SubObject subObject, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiSubObjects where subObjId = " + subObject.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				logger.warn("Exist another SbiSubObjects");
				return;
			}

			SbiObjects hibBIObj = new SbiObjects(biobj.getId());

			SbiBinContents hibBinContent = new SbiBinContents();
			hibBinContent.setId(subObject.getBinaryContentId());
			hibBinContent.setContent(subObject.getContent());

			SbiSubObjects sub=new SbiSubObjects();
			sub.setCreationDate(subObject.getCreationDate());
			sub.setDescription(subObject.getDescription());
			sub.setIsPublic(subObject.getIsPublic());
			sub.setName(subObject.getName());
			sub.setOwner(subObject.getOwner());
			sub.setLastChangeDate(subObject.getLastChangeDate());
			sub.setSbiBinContents(hibBinContent);
			sub.setSbiObject(hibBIObj);
			sub.setSubObjId(subObject.getId());

			session.save(sub);
			session.save(hibBinContent);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}	
	
	/*public void insertKpiDocuments(KpiDocuments docs, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			session.save(hibBIObj);
			tx.commit();
		}catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}*/

	/**
	 * Insert a biobject into the exported database.
	 * 
	 * @param biobj BIObject to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertBIObject(BIObject biobj, Session session) throws EMFUserError {
		logger.debug("IN");
		try {

			Query hibQuery = session.createQuery(" from SbiObjects where biobjId = " + biobj.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			Engine engine = biobj.getEngine();	
			SbiEngines hibEngine = (SbiEngines)session.load(SbiEngines.class, engine.getId());
			SbiDomains hibState = (SbiDomains)session.load(SbiDomains.class, biobj.getStateID());
			SbiDomains hibObjectType = (SbiDomains)session.load(SbiDomains.class, biobj.getBiObjectTypeID());
			SbiObjects hibBIObj = new SbiObjects(biobj.getId());
			hibBIObj.setSbiEngines(hibEngine); 
			hibBIObj.setDescr(biobj.getDescription());
			hibBIObj.setLabel(biobj.getLabel());
			hibBIObj.setName(biobj.getName());
			hibBIObj.setEncrypt(new Short(biobj.getEncrypt().shortValue()));
			hibBIObj.setRelName(biobj.getRelName());
			hibBIObj.setState(hibState);
			hibBIObj.setStateCode(biobj.getStateCode());
			hibBIObj.setObjectType(hibObjectType);
			hibBIObj.setObjectTypeCode(biobj.getBiObjectTypeCode());
			hibBIObj.setPath(biobj.getPath());
			hibBIObj.setUuid(biobj.getUuid());
			Integer visFlagIn = biobj.getVisible();
			Short visFlagSh = new Short(visFlagIn.toString());
			hibBIObj.setVisible(visFlagSh);
			Integer dataSourceId = biobj.getDataSourceId();
			if (dataSourceId != null) {
				SbiDataSource ds = (SbiDataSource) session.load(SbiDataSource.class, dataSourceId);
				hibBIObj.setDataSource(ds);
			}
			Integer dataSetId = biobj.getDataSetId();

			if (dataSetId != null) { 
				// if the transaction is new insert dataset if missing   
				IDataSetDAO datasetDao=DAOFactory.getDataSetDAO();
				IDataSet ds=datasetDao.loadDataSetByID(dataSetId);
				insertDataSet(ds, session);
				SbiDataSetConfig dataset = (SbiDataSetConfig) session.load(SbiDataSetConfig.class, dataSetId);
				hibBIObj.setDataSet(dataset);
			}

			hibBIObj.setCreationDate(biobj.getCreationDate());
			hibBIObj.setCreationUser(biobj.getCreationUser());
			hibBIObj.setRefreshSeconds(biobj.getRefreshSeconds());
			hibBIObj.setProfiledVisibility(biobj.getProfiledVisibility());
			Transaction tx = session.beginTransaction();
			session.save(hibBIObj);
			tx.commit();
			ObjTemplate template = biobj.getActiveTemplate();
			if (template == null) {
				logger.warn("Biobject with id = " + biobj.getId() + ", label = " + biobj.getLabel() + " and name = " + biobj.getName() + 
				" has not active template!!");
			} else {
				insertBIObjectTemplate(hibBIObj, template, session);
			}

		} catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert Object Template and Binary Content
	 * @param hibBIObj
	 * @param biobjTempl
	 * @param session
	 * @throws EMFUserError
	 */
	private void insertBIObjectTemplate(SbiObjects hibBIObj,ObjTemplate biobjTempl, Session session) throws EMFUserError {
		logger.debug("IN");

		try {
			boolean newTransaction=false;
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiObjTemplates where objTempId = " + biobjTempl.getBiobjId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			byte[] template = biobjTempl.getContent();

			SbiBinContents hibBinContent = new SbiBinContents();
			SbiObjTemplates hibObjTemplate = new SbiObjTemplates();
			hibObjTemplate.setObjTempId(biobjTempl.getBiobjId());
			hibBinContent.setId(biobjTempl.getBinId());
			hibBinContent.setContent(template);


			hibObjTemplate.setActive(new Boolean(true));
			hibObjTemplate.setCreationDate(biobjTempl.getCreationDate());
			hibObjTemplate.setCreationUser(biobjTempl.getCreationUser());
			hibObjTemplate.setDimension(biobjTempl.getDimension());
			hibObjTemplate.setName(biobjTempl.getName());
			hibObjTemplate.setProg(biobjTempl.getProg());
			hibObjTemplate.setSbiBinContents(hibBinContent);
			hibObjTemplate.setSbiObject(hibBIObj);

			session.save(hibBinContent);
			session.save(hibObjTemplate);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting biobject into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}	

	/**
	 * Insert a BIObject Parameter into the exported database.
	 * 
	 * @param biobjpar BIObject parameter to insert
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertBIObjectParameter(BIObjectParameter biobjpar,  Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			boolean newTransaction=false;
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiObjPar where objParId = " + biobjpar.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			/*
			Integer parid = biobjpar.getParameter().getId();
			Integer objid = biobj.getId();
			String query = " from SbiObjPar where id.sbiParameters.parId = " + parid +
						   " and id.sbiObjects.biobjId = " + objid;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiObjParId hibBIObjParId = new SbiObjParId();
			SbiParameters hibParameter = (SbiParameters)session.load(SbiParameters.class, parid);
			SbiObjects hibBIObject = (SbiObjects)session.load(SbiObjects.class, objid);
			hibBIObjParId.setSbiObjects(hibBIObject);
			hibBIObjParId.setSbiParameters(hibParameter);
			hibBIObjParId.setProg(new Integer(0));
			 */

			// build BI Object Parameter
			SbiObjPar hibBIObjPar = new SbiObjPar(biobjpar.getId());
			hibBIObjPar.setLabel(biobjpar.getLabel());
			hibBIObjPar.setReqFl(new Short(biobjpar.getRequired().shortValue()));
			hibBIObjPar.setModFl(new Short(biobjpar.getModifiable().shortValue()));
			hibBIObjPar.setViewFl(new Short(biobjpar.getVisible().shortValue()));
			hibBIObjPar.setMultFl(new Short(biobjpar.getMultivalue().shortValue()));
			hibBIObjPar.setParurlNm(biobjpar.getParameterUrlName());
			hibBIObjPar.setPriority(biobjpar.getPriority());
			hibBIObjPar.setProg(biobjpar.getProg());
			Integer biobjid = biobjpar.getBiObjectID();
			SbiObjects sbiob = (SbiObjects)session.load(SbiObjects.class, biobjid);
			Integer parid = biobjpar.getParID();			
			SbiParameters sbipar = (SbiParameters)session.load(SbiParameters.class, parid);
			hibBIObjPar.setSbiObject(sbiob);
			hibBIObjPar.setSbiParameter(sbipar);
			// save the BI Object Parameter
			session.save(hibBIObjPar);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting BIObjectParameter into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert a parameter into the exported database.
	 * 
	 * @param param The param object to insert
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertParameter(Parameter param, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			boolean newTransaction=false;
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiParameters where parId = " + param.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiDomains hibParamType = (SbiDomains)session.load(SbiDomains.class, param.getTypeId());
			SbiParameters hibParam = new SbiParameters(param.getId());
			hibParam.setDescr(param.getDescription());
			hibParam.setLength(new Short(param.getLength().shortValue()));
			hibParam.setLabel(param.getLabel());
			hibParam.setName(param.getName());
			hibParam.setParameterTypeCode(param.getType());
			hibParam.setMask(param.getMask());
			hibParam.setParameterType(hibParamType);
			hibParam.setFunctionalFlag(param.isFunctional() ? new Short((short) 1) : new Short((short) 0));
			hibParam.setTemporalFlag(param.isTemporal() ? new Short((short) 1) : new Short((short) 0));
			session.save(hibParam);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting parameter into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert a parameter use into the exported database.
	 * 
	 * @param parUse The Parameter use object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertParUse(ParameterUse parUse, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiParuse where useId = " + parUse.getUseID());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiParuse hibParuse = new SbiParuse(parUse.getUseID());
			// Set the relation with parameter
			SbiParameters hibParameters = (SbiParameters)session.load(SbiParameters.class, parUse.getId());
			hibParuse.setSbiParameters(hibParameters);
			// Set the relation with idLov (if the parameter ha a lov related)
			Integer lovId = parUse.getIdLov();
			if(lovId!=null){ 
				SbiLov hibLov = (SbiLov)session.load(SbiLov.class, parUse.getIdLov());
				hibParuse.setSbiLov(hibLov);
			}
			hibParuse.setLabel(parUse.getLabel());
			hibParuse.setName(parUse.getName());
			hibParuse.setDescr(parUse.getDescription());
			hibParuse.setManualInput(parUse.getManualInput());
			hibParuse.setSelectionType(parUse.getSelectionType());
			hibParuse.setMultivalue(parUse.isMultivalue()? new Integer(1): new Integer(0));
			session.save(hibParuse);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting parameter use into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert Dependencies between parameters.
	 * 
	 * @param biparams the biparams
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertBiParamDepend(List biparams, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Iterator iterBIParams = biparams.iterator();
			while(iterBIParams.hasNext()) {
				BIObjectParameter biparam = (BIObjectParameter)iterBIParams.next();			    
				IObjParuseDAO objparuseDao = DAOFactory.getObjParuseDAO();
				List objparlist = objparuseDao.loadObjParuses(biparam.getId());
				Iterator iterObjParuse = objparlist.iterator();
				while(iterObjParuse.hasNext()) {
					ObjParuse objparuse = (ObjParuse)iterObjParuse.next();
					Transaction tx = session.beginTransaction();
					// TODO controllare perch� serve questo controllo: le dipendenze non dovrebbero essere riutilizzabili, per 
					// cui vengono inseriti una sola volta
					Query hibQuery = session.createQuery(" from SbiObjParuse where id.sbiObjPar.objParId = " + objparuse.getObjParId() + 
							" and id.sbiParuse.useId = " + objparuse.getParuseId() + 
							" and id.sbiObjParFather.objParId = " + objparuse.getObjParFatherId() + 
							" and id.filterOperation = '" + objparuse.getFilterOperation() + "'" );
					List hibList = hibQuery.list();
					if(!hibList.isEmpty()) {
						continue;
					}
					// built key
					SbiObjParuseId hibObjParuseId = new SbiObjParuseId();
					SbiObjPar hibObjPar = (SbiObjPar)session.load(SbiObjPar.class, objparuse.getObjParId());
					SbiParuse hibParuse = (SbiParuse)session.load(SbiParuse.class, objparuse.getParuseId());
					SbiObjPar objparfather = (SbiObjPar)session.load(SbiObjPar.class, objparuse.getObjParFatherId());
					hibObjParuseId.setSbiObjPar(hibObjPar);
					hibObjParuseId.setSbiParuse(hibParuse);
					hibObjParuseId.setFilterOperation(objparuse.getFilterOperation());
					hibObjParuseId.setSbiObjParFather(objparfather);
					SbiObjParuse hibObjParuse = new SbiObjParuse(hibObjParuseId);
					hibObjParuse.setFilterColumn(objparuse.getFilterColumn());
					hibObjParuse.setProg(objparuse.getProg());
					hibObjParuse.setPreCondition(objparuse.getPreCondition());
					hibObjParuse.setPostCondition(objparuse.getPostCondition());
					hibObjParuse.setLogicOperator(objparuse.getLogicOperator());
					session.save(hibObjParuse);
					tx.commit();	
				}
			}

		} catch (Exception e) {
			logger.error("Error while inserting parameter dependencied into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}







	/**
	 * Insert a list of value into the exported database.
	 * 
	 * @param lov The list of values object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertLov(ModalitiesValue lov, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			boolean newTransaction=false;
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiLov where lovId = " + lov.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiLov hibLov = new SbiLov(lov.getId());
			hibLov.setName(lov.getName());
			hibLov.setLabel(lov.getLabel());
			hibLov.setDescr(lov.getDescription());
			SbiDomains inpType = (SbiDomains)session.load(SbiDomains.class, new Integer(lov.getITypeId()));
			hibLov.setInputType(inpType);
			hibLov.setInputTypeCd(lov.getITypeCd());
			hibLov.setLovProvider(lov.getLovProvider());
			session.save(hibLov);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting lov into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert a check into the exported database.
	 * 
	 * @param check The check object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertCheck(Check check, Session session) throws EMFUserError {
		logger.debug("IN");
		try{
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiChecks where checkId = " + check.getCheckId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiDomains checkType = (SbiDomains)session.load(SbiDomains.class, check.getValueTypeId());
			SbiChecks hibCheck = new SbiChecks(check.getCheckId());
			hibCheck.setCheckType(checkType);
			hibCheck.setDescr(check.getDescription());
			hibCheck.setName(check.getName());
			hibCheck.setLabel(check.getLabel());
			hibCheck.setValue1(check.getFirstValue());
			hibCheck.setValue2(check.getSecondValue());
			hibCheck.setValueTypeCd(check.getValueTypeCd());
			session.save(hibCheck);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting check into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert an association between a parameter use and a check into the exported database.
	 * 
	 * @param parUse The paruse object which is an element of the association
	 * @param check The check object which is an element of the association
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertParuseCheck(ParameterUse parUse, Check check, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Integer paruseId = parUse.getUseID();
			Integer checkId = check.getCheckId();
			String query = " from SbiParuseCk where id.sbiParuse.useId = " + paruseId +
			" and id.sbiChecks.checkId = " + checkId;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiParuseCkId hibParuseCkId = new SbiParuseCkId();
			SbiChecks hibChecks = (SbiChecks)session.load(SbiChecks.class, check.getCheckId());
			SbiParuse hibParuse = (SbiParuse)session.load(SbiParuse.class, parUse.getUseID());
			hibParuseCkId.setSbiChecks(hibChecks);
			hibParuseCkId.setSbiParuse(hibParuse);
			SbiParuseCk hibParuseCheck = new SbiParuseCk(hibParuseCkId);
			hibParuseCheck.setProg(new Integer(0));
			session.save(hibParuseCheck);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting paruse and check association into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert an association between a parameter use and a role into the exported database.
	 * 
	 * @param parUse The paruse object which is an element of the association
	 * @param role The role object which is an element of the association
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertParuseRole(ParameterUse parUse, Role role, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Integer paruseId = parUse.getUseID();
			Integer roleId = role.getId();
			String query = " from SbiParuseDet where id.sbiParuse.useId = " + paruseId +
			" and id.sbiExtRoles.extRoleId = " + roleId;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiParuseDetId hibParuseDetId = new SbiParuseDetId();
			SbiParuse hibParuse = (SbiParuse)session.load(SbiParuse.class, parUse.getUseID());
			SbiExtRoles hibExtRole = (SbiExtRoles)session.load(SbiExtRoles.class, role.getId());
			hibParuseDetId.setSbiExtRoles(hibExtRole);
			hibParuseDetId.setSbiParuse(hibParuse);
			SbiParuseDet hibParuseDet = new SbiParuseDet(hibParuseDetId);
			session.save(hibParuseDet);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting paruse and role association into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert an association between a master report and a subreport.
	 * 
	 * @param sub The subreport
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertSubReportAssociation(Subreport sub, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();

			Integer masterId = sub.getMaster_rpt_id();
			Integer subId = sub.getSub_rpt_id();
			String query = " from SbiSubreports as subreport where " +
			"subreport.id.masterReport.biobjId = " + masterId + " and " +
			"subreport.id.subReport.biobjId = " + subId;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			SbiSubreportsId hibSubreportid = new SbiSubreportsId();
			SbiObjects masterReport = (SbiObjects) session.load(SbiObjects.class, sub.getMaster_rpt_id());
			SbiObjects subReport = (SbiObjects) session.load(SbiObjects.class, sub.getSub_rpt_id());
			hibSubreportid.setMasterReport(masterReport);
			hibSubreportid.setSubReport(subReport);
			SbiSubreports hibSubreport = new SbiSubreports(hibSubreportid);
			session.save(hibSubreport);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting subreport " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert a functionality into the exported database.
	 * 
	 * @param funct Functionality Object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertFunctionality(LowFunctionality funct, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiFunctions where funct_id = " + funct.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			IDomainDAO domDAO = DAOFactory.getDomainDAO();
			Domain functTypeDom = domDAO.loadDomainByCodeAndValue("FUNCT_TYPE", funct.getCodType());
			SbiDomains hibFunctType = (SbiDomains)session.load(SbiDomains.class, functTypeDom.getValueId());
			SbiFunctions hibFunct = new SbiFunctions(funct.getId());
			hibFunct.setCode(funct.getCode());
			hibFunct.setDescr(funct.getDescription());
			hibFunct.setFunctTypeCd(funct.getCodType());
			hibFunct.setFunctType(hibFunctType);
			hibFunct.setName(funct.getName());
			hibFunct.setPath(funct.getPath());
			hibFunct.setProg(funct.getProg());
			session.save(hibFunct);
			tx.commit();
			Role[] devRoles = funct.getDevRoles();
			Domain devDom = domDAO.loadDomainByCodeAndValue("STATE", "DEV");
			for(int i=0; i<devRoles.length; i++) {
				Role devRole = devRoles[i];
				insertRole(devRole, session);
				insertFunctRole(devRole, funct, devDom.getValueId(), devDom.getValueCd(), session);
			}
			Role[] testRoles = funct.getTestRoles();
			Domain testDom = domDAO.loadDomainByCodeAndValue("STATE", "TEST");
			for(int i=0; i<testRoles.length; i++) {
				Role testRole = testRoles[i];
				insertRole(testRole, session);
				insertFunctRole(testRole, funct, testDom.getValueId(), testDom.getValueCd(), session);
			}
			Role[] execRoles = funct.getExecRoles();
			Domain execDom = domDAO.loadDomainByCodeAndValue("STATE", "REL");
			for(int i=0; i<execRoles.length; i++) {
				Role execRole = execRoles[i];
				insertRole(execRole, session);
				insertFunctRole(execRole, funct, execDom.getValueId(), execDom.getValueCd(), session);
			}

		} catch (Exception e) {
			logger.error("Error while inserting Functionality into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}

		// recursively insert parent functionalities
		Integer parentId = funct.getParentId();
		if(parentId!=null){
			ILowFunctionalityDAO lowFunctDAO = DAOFactory.getLowFunctionalityDAO();
			LowFunctionality functPar = lowFunctDAO.loadLowFunctionalityByID(parentId, false);
			insertFunctionality(functPar, session);
		}
		logger.debug("OUT");

	}





	/**
	 * Insert a role into the exported database.
	 * 
	 * @param role The role object to export
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertRole(Role role, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Query hibQuery = session.createQuery(" from SbiExtRoles where extRoleId = " + role.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			SbiExtRoles hibRole = new SbiExtRoles(role.getId());
			hibRole.setCode(role.getCode());
			hibRole.setDescr(role.getDescription());
			hibRole.setName(role.getName());
			SbiDomains roleType = (SbiDomains)session.load(SbiDomains.class, role.getRoleTypeID());
			hibRole.setRoleType(roleType);
			hibRole.setRoleTypeCode(role.getRoleTypeCD());
			hibRole.setIsAbleToSaveIntoPersonalFolder(new Boolean(role.isAbleToSaveIntoPersonalFolder()));
			hibRole.setIsAbleToSaveRememberMe(new Boolean(role.isAbleToSaveRememberMe()));
			hibRole.setIsAbleToSeeMetadata(new Boolean(role.isAbleToSeeMetadata()));
			hibRole.setIsAbleToSeeNotes(new Boolean(role.isAbleToSeeNotes()));;
			hibRole.setIsAbleToSeeSnapshots(new Boolean(role.isAbleToSeeSnapshots()));
			hibRole.setIsAbleToSeeSubobjects(new Boolean(role.isAbleToSeeSubobjects()));
			hibRole.setIsAbleToSeeViewpoints(new Boolean(role.isAbleToSeeViewpoints()));
			hibRole.setIsAbleToSendMail(new Boolean(role.isAbleToSendMail()));
			hibRole.setIsAbleToBuildQbeQuery(role.isAbleToBuildQbeQuery());
			session.save(hibRole);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting role into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert an association between a functionality and a role into the exported database.
	 * 
	 * @param role The role object which is an element of the association
	 * @param funct The functionality object which is an element of the association
	 * @param stateId The id of the State associated to the couple role / functionality
	 * @param stateCD The code of the State associated to the couple role / functionality
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertFunctRole(Role role, LowFunctionality funct, Integer stateId, String stateCD, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			Integer roleid = role.getId();
			Integer functid = funct.getId();
			String query = " from SbiFuncRole where id.function = " + functid +
			" and id.role = " + roleid + " and id.state = " + stateId ;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiFuncRoleId hibFuncRoleId = new SbiFuncRoleId();
			SbiFunctions hibFunct = (SbiFunctions)session.load(SbiFunctions.class, funct.getId());
			SbiExtRoles hibRole = (SbiExtRoles)session.load(SbiExtRoles.class, role.getId());
			SbiDomains hibState = (SbiDomains)session.load(SbiDomains.class, stateId);
			hibFuncRoleId.setFunction(hibFunct);
			hibFuncRoleId.setRole(hibRole);
			hibFuncRoleId.setState(hibState);
			SbiFuncRole hibFunctRole = new SbiFuncRole(hibFuncRoleId);
			hibFunctRole.setStateCd(stateCD);
			session.save(hibFunctRole);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting function and role association into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert an association between a functionality and a biobject into the exported database.
	 * 
	 * @param biobj The BIObject which is an element of the association
	 * @param funct The functionality object which is an element of the association
	 * @param session Hibernate session for the exported database
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertObjFunct(BIObject biobj, LowFunctionality funct, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();

			Integer biobjid = biobj.getId();
			Integer functid = funct.getId();
			String query = " from SbiObjFunc where id.sbiFunctions = " + functid +
			" and id.sbiObjects = " + biobjid;
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// built key
			SbiObjFuncId hibObjFunctId = new SbiObjFuncId();
			SbiFunctions hibFunct = (SbiFunctions)session.load(SbiFunctions.class, funct.getId());
			SbiObjects hibObj = (SbiObjects)session.load(SbiObjects.class, biobj.getId());
			hibObjFunctId.setSbiObjects(hibObj);
			hibObjFunctId.setSbiFunctions(hibFunct);
			SbiObjFunc hibObjFunct = new SbiObjFunc(hibObjFunctId);
			hibObjFunct.setProg(new Integer(0));
			session.save(hibObjFunct);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting function and object association into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Exports the map catalogue (maps and features)
	 * 
	 * @param session Hibernate session for the exported database
	 * @throws EMFUserError
	 */
	public void insertMapCatalogue(Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			// controls if the maps are already inserted into export db
			Transaction tx = session.beginTransaction();
			String query = " from SbiGeoMaps";
			Query hibQuery = session.createQuery(query);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				// maps are already exported
				return;
			}
			tx.commit();

			insertMaps(session);
			insertFeatures(session);
			insertMapFeaturesAssociations(session);

		} catch (Exception e) {
			logger.error("Error while inserting map catalogue into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		} finally{
			logger.debug("OUT");
		}

	}

	/**
	 * Insert the maps of the maps catalogue
	 * 
	 * @param session Hibernate session for the exported database
	 * @throws EMFUserError
	 */
	private void insertMaps(Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			ISbiGeoMapsDAO mapsDAO = DAOFactory.getSbiGeoMapsDAO();
			List allMaps = mapsDAO.loadAllMaps();
			Iterator mapsIt = allMaps.iterator();
			while (mapsIt.hasNext()) {
				GeoMap map = (GeoMap) mapsIt.next();
				SbiGeoMaps hibMap = new SbiGeoMaps(map.getMapId());
				hibMap.setDescr(map.getDescr());
				hibMap.setFormat(map.getFormat());
				hibMap.setName(map.getName());
				hibMap.setUrl(map.getUrl());

				if (map.getBinId() == 0) {
					logger.warn("Map with id = " + map.getMapId() + " and name = " + map.getName() + 
					" has not binary content!!");
					hibMap.setBinContents(null);
				} else {
					SbiBinContents hibBinContent = new SbiBinContents();
					hibBinContent.setId(map.getBinId());
					byte[] content = DAOFactory.getBinContentDAO().getBinContent(map.getBinId());
					hibBinContent.setContent(content);
					hibMap.setBinContents(hibBinContent);

					session.save(hibBinContent);
				}

				session.save(hibMap);

			}
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting maps into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		} finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert the features of the maps catalogue
	 * 
	 * @param session Hibernate session for the exported database
	 * @throws EMFUserError
	 */
	private void insertFeatures(Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			ISbiGeoFeaturesDAO featuresDAO = DAOFactory.getSbiGeoFeaturesDAO();
			List allFeatures = featuresDAO.loadAllFeatures();
			Iterator featureIt = allFeatures.iterator();
			while (featureIt.hasNext()) {
				GeoFeature feature = (GeoFeature) featureIt.next();
				SbiGeoFeatures hibFeature = new SbiGeoFeatures(feature.getFeatureId());
				hibFeature.setDescr(feature.getDescr());
				hibFeature.setName(feature.getName());
				hibFeature.setType(feature.getType());
				session.save(hibFeature);
			}
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting features into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		} finally{
			logger.debug("OUT");
		}
	}

	/**
	 * Insert the association between maps and features of the maps catalogue
	 * 
	 * @param session Hibernate session for the exported database
	 * @throws EMFUserError
	 */
	private void insertMapFeaturesAssociations(Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Transaction tx = session.beginTransaction();
			ISbiGeoMapsDAO mapsDAO = DAOFactory.getSbiGeoMapsDAO();
			List allMaps = mapsDAO.loadAllMaps();
			ISbiGeoMapFeaturesDAO mapFeaturesDAO = DAOFactory.getSbiGeoMapFeaturesDAO();
			Iterator mapsIt = allMaps.iterator();
			while (mapsIt.hasNext()) {
				GeoMap map = (GeoMap) mapsIt.next();
				List mapFeatures = mapFeaturesDAO.loadFeaturesByMapId(new Integer(map.getMapId()));
				Iterator mapFeaturesIt = mapFeatures.iterator();
				while (mapFeaturesIt.hasNext()) {
					GeoFeature feature = (GeoFeature) mapFeaturesIt.next();
					GeoMapFeature mapFeature = mapFeaturesDAO.loadMapFeatures(new Integer(map.getMapId()), new Integer(feature.getFeatureId()));
					SbiGeoMapFeatures hibMapFeature = new SbiGeoMapFeatures();	
					SbiGeoMapFeaturesId hibMapFeatureId = new SbiGeoMapFeaturesId();			
					hibMapFeatureId.setMapId(mapFeature.getMapId());
					hibMapFeatureId.setFeatureId(mapFeature.getFeatureId());
					hibMapFeature.setId(hibMapFeatureId);
					hibMapFeature.setSvgGroup(mapFeature.getSvgGroup());
					hibMapFeature.setVisibleFlag(mapFeature.getVisibleFlag());
					session.save(hibMapFeature);
				}
			}
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting association between maps and features into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		} finally{
			logger.debug("OUT");
		}
	}






	/**
	 * Insert Model Instance Tree.
	 * 
	 * @param mi the Model Instance
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public List insertAllFromModelInstance(ModelInstance mi, Session session) throws EMFUserError {
		logger.debug("IN");

		biObjectToInsert=new ArrayList();

		//I want to insert the whole model instance tree, first of all I get the model instance root
		IModelInstanceDAO modInstDAO=DAOFactory.getModelInstanceDAO();
		ModelInstance miRoot=modInstDAO.loadModelInstanceRoot(mi);


		// insert the model (model instance root points to model root)
		logger.debug("Insert the model root and the tree");		

		// isnert all kpi model attribute thought are related to domains!
		insertAllKpiModelAttr(session);
		// fill the array with models with attributes
		modelsWithAttributeValued = DAOFactory.getSbiKpiModelAttrValDAO().allModelsIdWithAttribute();

		// insert model tree starting from root.
		Model modelRoot=miRoot.getModel();
		insertModelTree(modelRoot, session);

		logger.debug("Insert the model Instance root and the tree");		

		//insert the Model Instanceroot
		insertModelInstanceTree(miRoot, session);

		logger.debug("OUT");
		return biObjectToInsert;
	}










	/**
	 * Insert Model Instance.
	 * 
	 * @param mi the Model Instance
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertModelInstanceTree(ModelInstance mi, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiModelInst where kpiModelInst = " + mi.getId());

			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			// main attributes			
			SbiKpiModelInst hibMi = new SbiKpiModelInst();
			hibMi.setKpiModelInst(mi.getId());
			hibMi.setName(mi.getName());
			hibMi.setLabel(mi.getLabel());
			hibMi.setDescription(mi.getDescription());
			hibMi.setStartDate(mi.getStartDate());
			hibMi.setEndDate(mi.getEndDate());
			hibMi.setModelUUID(mi.getModelUUID());

			// insert Parent
			if(mi.getParentId()!=null){
				SbiKpiModelInst hibKpiModelInstParent = (SbiKpiModelInst) session.load(SbiKpiModelInst.class, mi.getParentId());
				hibMi.setSbiKpiModelInst(hibKpiModelInstParent);
			}

			// model
			if(mi.getModel()!=null){
				SbiKpiModel hibModel = (SbiKpiModel) session.load(SbiKpiModel.class, mi.getModel().getId());
				hibMi.setSbiKpiModel(hibModel);
			}

			// Load tKpi Instance
			if (mi.getKpiInstance() != null) {
				KpiInstance kpiInstance=mi.getKpiInstance();
				insertKpiInstance(kpiInstance.getKpiInstanceId(), session);
				SbiKpiInstance hibKpiInst = (SbiKpiInstance) session.load(SbiKpiInstance.class, kpiInstance.getKpiInstanceId());
				hibMi.setSbiKpiInstance(hibKpiInst);

			}

			Transaction tx = session.beginTransaction();
			session.save(hibMi);
			tx.commit();

			// Load all the model resources of the current instance model
			// after having inserted model instance
			IModelResourceDAO modelResourceDao=DAOFactory.getModelResourcesDAO();			
			List modelResources=modelResourceDao.loadModelResourceByModelId(mi.getId());
			for (Iterator iterator = modelResources.iterator(); iterator.hasNext();) {
				ModelResources modRes = (ModelResources) iterator.next();
				insertModelResources(modRes, session);
				// TODO: maybe insert also the set
			}

			Set modelInstanceChildren=new HashSet();
			logger.debug("insert current model instance children");
			// get the Model Instance children
			IModelInstanceDAO modelInstDao=DAOFactory.getModelInstanceDAO();
			ModelInstance modInstWithChildren=modelInstDao.loadModelInstanceWithChildrenById(mi.getId());
			List childrenList=modInstWithChildren.getChildrenNodes();
			if(childrenList!=null){
				for (Iterator iterator = childrenList.iterator(); iterator.hasNext();) {
					ModelInstance childNode = (ModelInstance) iterator.next();
					logger.debug("insert child "+childNode.getLabel());
					insertModelInstanceTree(childNode,session);				
					SbiKpiModelInst hibKpiModelInst = (SbiKpiModelInst) session.load(SbiKpiModelInst.class, childNode.getId());
					modelInstanceChildren.add(hibKpiModelInst);
				}
			}
			hibMi.setSbiKpiModelInsts(modelInstanceChildren);  // serve?




		} catch (Exception e) {
			logger.error("Error while inserting ModelInstance tree into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert Model .
	 * 
	 * @param mod the Model
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertModelTree(Model mod, Session session) throws EMFUserError {
		logger.debug("IN");
		IModelDAO modelDao=DAOFactory.getModelDAO();
		try {



			Query hibQuery = session.createQuery(" from SbiKpiModel where kpiModelId = " + mod.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}


			// main attributes			
			SbiKpiModel hibMod = new SbiKpiModel();
			hibMod.setKpiModelId(mod.getId());
			hibMod.setKpiModelLabel(mod.getLabel());
			hibMod.setKpiModelCd(mod.getCode());
			hibMod.setKpiModelDesc(mod.getDescription());
			hibMod.setKpiModelNm(mod.getName());

			// insert Parent
			if(mod.getParentId()!=null){
				SbiKpiModel hibKpiModelParent = (SbiKpiModel) session.load(SbiKpiModel.class, mod.getParentId());
				hibMod.setSbiKpiModel(hibKpiModelParent);

			}

			// sbiDomain
			Criterion nameCriterrion = Expression.eq("valueCd", mod.getTypeCd());
			Criteria criteria = session.createCriteria(SbiDomains.class);
			criteria.add(nameCriterrion);	
			SbiDomains domainType = (SbiDomains) criteria.uniqueResult();
			hibMod.setModelType(domainType);

			// load kpi
			if (mod.getKpiId() != null) {
				Integer kpiId=mod.getKpiId();
				insertKpi(kpiId,session);
				SbiKpi sbiKpi= (SbiKpi) session.load(SbiKpi.class, mod.getKpiId());
				hibMod.setSbiKpi(sbiKpi);
			}

			// save current Model
			Transaction tx = session.beginTransaction();
			session.save(hibMod);
			tx.commit();
			logger.debug("current model "+mod.getCode()+" inserted");

			Set modelChildren=new HashSet();
			logger.debug("insert current model children");

			//Load model childred
			Model modWithChildren=modelDao.loadModelWithChildrenById(mod.getId());

			List childrenList=modWithChildren.getChildrenNodes();
			if(childrenList!=null){
				for (Iterator iterator = childrenList.iterator(); iterator.hasNext();) {
					Model childNode = (Model) iterator.next();
					logger.debug("insert child "+childNode.getCode());
					insertModelTree(childNode,session);				
					SbiKpiModel hibKpiModel = (SbiKpiModel) session.load(SbiKpiModel.class, childNode.getId());
					modelChildren.add(hibKpiModel);
				}
			}
			hibMod.setSbiKpiModels(modelChildren);

			// Load Model Attribute Values
			List attributes = mod.getModelAttributes();

			// check if actual model has attributes value
			if(modelsWithAttributeValued.contains(mod.getId())){
				for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
					ModelAttribute modelAttribute = (ModelAttribute) iterator.next();
					insertKpiModelAttrVal(modelAttribute, mod.getId(), session);
				}
			}
			//insertKpiModelAttr(modAttribute, session)



		} catch (Exception e) {
			logger.error("Error while inserting Model into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert Kpi .
	 * 
	 * @param kpi the Kpi
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertKpi(Integer kpiId, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpi where kpiId = " + kpiId);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}
			// get the Kpi BO from id
			IKpiDAO kpiDao=DAOFactory.getKpiDAO();
			Kpi kpi=kpiDao.loadKpiById(kpiId);

			// main attributes			
			SbiKpi hibKpi = new SbiKpi();
			hibKpi.setKpiId(kpi.getKpiId());
			hibKpi.setCode(kpi.getCode());
			hibKpi.setDescription(kpi.getDescription());
			hibKpi.setInterpretation(kpi.getInterpretation());
			hibKpi.setName(kpi.getKpiName());
			// Weight???	hibKpi.setWeight(kpi.get)
			hibKpi.setWeight(kpi.getStandardWeight());
			char isFather=kpi.getIsParent().equals(true)? 'T' : 'F';
			hibKpi.setFlgIsFather(new Character(isFather));
			hibKpi.setInterpretation(kpi.getInterpretation());
			hibKpi.setInputAttributes(kpi.getInputAttribute());
			hibKpi.setModelReference(kpi.getModelReference());
			hibKpi.setTargetAudience(kpi.getTargetAudience());


			if(kpi.getMeasureTypeId()!=null){
				SbiDomains measureType=(SbiDomains)session.load(SbiDomains.class, kpi.getMeasureTypeId());			
				hibKpi.setSbiDomainsByMeasureType(measureType);
			}
			if(kpi.getKpiTypeId()!=null){
				SbiDomains kpiType=(SbiDomains)session.load(SbiDomains.class, kpi.getKpiTypeId());			
				hibKpi.setSbiDomainsByKpiType(kpiType);
			}
			if(kpi.getMetricScaleId()!=null){
				SbiDomains metricScaleType=(SbiDomains)session.load(SbiDomains.class, kpi.getMetricScaleId());			
				hibKpi.setSbiDomainsByMetricScaleType(metricScaleType);
			}

			// load dataset
			if (kpi.getKpiDsId() != null) {    
				Integer dsID = kpi.getKpiDsId();				
				IDataSet ds=DAOFactory.getDataSetDAO().loadDataSetByID(dsID);
				insertDataSet(ds, session);
				SbiDataSetConfig sbiDs= (SbiDataSetConfig) session.load(SbiDataSetConfig.class, ds.getId());
				hibKpi.setSbiDataSet(sbiDs);
			}

			// load threshold
			if (kpi.getThreshold() != null) {
				Threshold th=kpi.getThreshold();
				insertThreshold(th, session);
				SbiThreshold sbiTh= (SbiThreshold) session.load(SbiThreshold.class, th.getId());
				hibKpi.setSbiThreshold(sbiTh);
			}

			// Load BI Object
			//TODO lista documenti
			if(kpi.getSbiKpiDocuments()!=null && !kpi.getSbiKpiDocuments().isEmpty() ){
				List kpiDocsList = kpi.getSbiKpiDocuments();
				Set sbiKpiDocumentses = new HashSet(0);
				Iterator i = kpiDocsList.iterator();
				while (i.hasNext()) {
					
					KpiDocuments doc = (KpiDocuments) i.next();					
					String label = doc.getBiObjLabel();					
					
					IBIObjectDAO biobjDAO = DAOFactory.getBIObjectDAO();
					BIObject biobj = biobjDAO.loadBIObjectByLabel(label);
					if(biobj!=null){
						insertBIObject(biobj, session);
						doc.setBiObjId(biobj.getId());
						//biObjectToInsert.add(biobj.getId());					
					}						
				}
			}
			
			// Measure Unit   ???
			if(kpi.getScaleCode()!=null && !kpi.getScaleCode().equalsIgnoreCase("")){
				IMeasureUnitDAO muDao=DAOFactory.getMeasureUnitDAO();
				MeasureUnit mu=muDao.loadMeasureUnitByCd(kpi.getScaleCode());
				insertMeasureUnit(mu, session);
				SbiMeasureUnit sbiMu= (SbiMeasureUnit) session.load(SbiMeasureUnit.class, mu.getId());
				hibKpi.setSbiMeasureUnit(sbiMu);
			}

			Transaction tx = session.beginTransaction();
			Integer kpiIdReturned = (Integer)session.save(hibKpi);
			tx.commit();
			
			List kpiDocsList = kpi.getSbiKpiDocuments();
			Iterator i = kpiDocsList.iterator();
			while (i.hasNext()) {
				KpiDocuments doc = (KpiDocuments) i.next();
				String label = doc.getBiObjLabel();
				Integer origDocId = doc.getBiObjId();
				Criterion labelCriterrion = Expression.eq("label",label);
				Criteria criteria = session.createCriteria(SbiObjects.class);
				criteria.add(labelCriterrion);
				SbiObjects hibObject = (SbiObjects) criteria.uniqueResult();
				
				if(hibObject!=null){
					SbiKpiDocument temp = new SbiKpiDocument();
					temp.setSbiKpi(hibKpi);
					temp.setSbiObjects(hibObject);
					KpiDocuments docK = kpiDao.loadKpiDocByKpiIdAndDocId(kpiId, origDocId);
					if(docK!=null && docK.getKpiDocId()!=null){
						temp.setIdKpiDoc(docK.getKpiDocId());
						Transaction tx2 = session.beginTransaction();
						session.save(temp);
						tx2.commit();
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("Error while inserting kpi into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}




	/**
	 * Insert Kpi Instance.
	 * 
	 * @param kpiInst the Kpi Instance
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertKpiInstance(Integer kpiInstId, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiInstance where idKpiInstance = " + kpiInstId);
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			}

			// recover kpi instance from Id
			IKpiInstanceDAO kpiInstDAO=DAOFactory.getKpiInstanceDAO();
			KpiInstance kpiInst=kpiInstDAO.loadKpiInstanceById(kpiInstId);

			// main attributes			
			SbiKpiInstance hibKpiInst = new SbiKpiInstance();
			hibKpiInst.setIdKpiInstance(kpiInst.getKpiInstanceId());
			hibKpiInst.setBeginDt(kpiInst.getD());
			hibKpiInst.setWeight(kpiInst.getWeight());
			hibKpiInst.setTarget(kpiInst.getTarget());

			if(kpiInst.getChartTypeId()!=null){
				SbiDomains chartType=(SbiDomains)session.load(SbiDomains.class, kpiInst.getChartTypeId());			
				hibKpiInst.setChartType(chartType);
			}

			// Kpi
			if (kpiInst.getKpi()!=null) {    
				insertKpi(kpiInst.getKpi(), session);
				SbiKpi sbiKpi= (SbiKpi) session.load(SbiKpi.class, kpiInst.getKpi());
				hibKpiInst.setSbiKpi(sbiKpi);
			}

			// load threshold
			if (kpiInst.getThresholdId() != null) {
				IThresholdDAO thresholdDAO=DAOFactory.getThresholdDAO();
				Threshold th=thresholdDAO.loadThresholdById(kpiInst.getThresholdId());
				insertThreshold(th, session);
				SbiThreshold sbiTh= (SbiThreshold) session.load(SbiThreshold.class, th.getId());
				hibKpiInst.setSbiThreshold(sbiTh);
			}

			// load measureUnit!
			if(kpiInst.getScaleCode()!=null){
				IMeasureUnitDAO muDao=DAOFactory.getMeasureUnitDAO();
				MeasureUnit mu=muDao.loadMeasureUnitByCd(kpiInst.getScaleCode());
				insertMeasureUnit(mu, session);
				SbiMeasureUnit sbiMu= (SbiMeasureUnit) session.load(SbiMeasureUnit.class, mu.getId());
				hibKpiInst.setSbiMeasureUnit(sbiMu);
			}

			// Insert KPI Instance

			Transaction tx = session.beginTransaction();
			session.save(hibKpiInst);
			tx.commit();



			// after inserted Kpi Instance insert periods		
			// load all alarms
			ISbiAlarmDAO sbiAlarmDAO=DAOFactory.getAlarmDAO();
			List<Alarm> alarmsToLoad=sbiAlarmDAO.loadAllByKpiInstId(kpiInstId);
			for (Iterator iterator = alarmsToLoad.iterator(); iterator.hasNext();) {
				Alarm alarm = (Alarm) iterator.next();
				insertAlarm(alarm, session);				

			}


			// after inserted Kpi Instance insert periods
			// Load all the kpi inst period and the periodicity s well
			IKpiInstPeriodDAO kpiInstPeriodDao=DAOFactory.getKpiInstPeriodDAO();			
			List kpiInstPeriodList=kpiInstPeriodDao.loadKpiInstPeriodId(kpiInst.getKpiInstanceId());
			for (Iterator iterator = kpiInstPeriodList.iterator(); iterator.hasNext();) {
				KpiInstPeriod modKpiInst = (KpiInstPeriod) iterator.next();
				insertKpiInstancePeriod(modKpiInst, session);
			}



		} catch (Exception e) {
			logger.error("Error while inserting kpi instance into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert Threshold .
	 * 
	 * @param th the Threshold
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertThreshold(Threshold th, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiThreshold where thresholdId = " + th.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			SbiDomains thresholdType=(SbiDomains)session.load(SbiDomains.class, th.getThresholdTypeId());

			// main attributes			
			SbiThreshold hibTh = new SbiThreshold();
			hibTh.setThresholdId(th.getId());
			hibTh.setName(th.getName());
			hibTh.setCode(th.getCode());
			hibTh.setDescription(th.getDescription());
			hibTh.setThresholdType(thresholdType);
			Transaction tx = session.beginTransaction();
			session.save(hibTh);
			tx.commit();

			// load Threshold Value
			if (th.getThresholdValues() != null && th.getThresholdValues().size()>0) {
				Set thresholdValues=new HashSet(0);
				for (Iterator iterator = th.getThresholdValues().iterator(); iterator.hasNext();) {
					ThresholdValue thValue = (ThresholdValue) iterator.next();
					insertThresholdValue(thValue, session, hibTh);
					Integer thValueId=thValue.getId();
					SbiThresholdValue sbiTh= (SbiThresholdValue) session.load(SbiThresholdValue.class, thValue.getId());
					thresholdValues.add(sbiTh);
				}
				//hibTh.setSbiThresholdValues(thresholdValues);
			}

		} catch (Exception e) {
			logger.error("Error while inserting dataSource into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert Threshold Value.
	 * 
	 * @param th the Threshold Value
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertThresholdValue(ThresholdValue thValue, Session session, SbiThreshold sbiTh) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiThresholdValue where idThresholdValue = " + thValue.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			SbiDomains severity=(SbiDomains)session.load(SbiDomains.class, thValue.getSeverityId());

			// main attributes			
			SbiThresholdValue hibThValue = new SbiThresholdValue();
			hibThValue.setIdThresholdValue(thValue.getId());
			hibThValue.setLabel(thValue.getLabel());
			hibThValue.setMaxValue(thValue.getMaxValue());
			hibThValue.setMinValue(thValue.getMinValue());
			hibThValue.setMinClosed(thValue.getMinClosed());
			hibThValue.setMaxClosed(thValue.getMaxClosed());
			hibThValue.setThValue(thValue.getValue());

			//Color col=thValue.getColour();
			//String colour = "rgb("+col.getRed()+", "+col.getGreen()+", "+col.getBlue()+")" ;
			String colour=thValue.getColourString();
			hibThValue.setColour(colour);
			hibThValue.setPosition(thValue.getPosition());
			hibThValue.setSeverity(severity);

			// put association with Threshold
			hibThValue.setSbiThreshold(sbiTh);

			Transaction tx = session.beginTransaction();			
			session.save(hibThValue);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting threshold value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert Measure Unit.
	 * 
	 * @param mu the Measure Unit
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertMeasureUnit(MeasureUnit mu, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiMeasureUnit where idMeasureUnit = " + mu.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiMeasureUnit hibMu = new SbiMeasureUnit();
			hibMu.setIdMeasureUnit(mu.getId());
			hibMu.setName(mu.getName());
			hibMu.setScaleCd(mu.getScaleCd());
			hibMu.setScaleNm(mu.getScaleNm());

			SbiDomains scaleType=(SbiDomains)session.load(SbiDomains.class, mu.getScaleTypeId());

			hibMu.setScaleType(scaleType);

			Transaction tx = session.beginTransaction();			
			session.save(hibMu);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting threshold value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}




	/**
	 * Insert Kpi Instance Periodicity.
	 * 
	 * @param kpiPeriod kpiInstancePeriodicity
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertKpiInstancePeriod( KpiInstPeriod kpiInstPeriod, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiInstPeriod where kpiInstPeriodId = " + kpiInstPeriod.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiKpiInstPeriod hibKpiInstPeriod = new SbiKpiInstPeriod();
			hibKpiInstPeriod.setKpiInstPeriodId(kpiInstPeriod.getId());
			hibKpiInstPeriod.setDefault_(kpiInstPeriod.getDefaultValue());

			// Kpi instance should be already inserted

			if (kpiInstPeriod.getKpiInstId()!= null) {
				Integer kpiInstPeriodId=kpiInstPeriod.getKpiInstId();
				SbiKpiInstance sbiKpiInstance= (SbiKpiInstance) session.load(SbiKpiInstance.class, kpiInstPeriodId);
				if(sbiKpiInstance!=null){
					hibKpiInstPeriod.setSbiKpiInstance(sbiKpiInstance);
				}
			}

			// load Periodicity

			if (kpiInstPeriod.getPeriodicityId() != null) {
				Integer periodicityId=kpiInstPeriod.getPeriodicityId();
				IPeriodicityDAO periodicityDAO=DAOFactory.getPeriodicityDAO();
				Periodicity period=periodicityDAO.loadPeriodicityById(periodicityId);
				insertPeriodicity(period, session);
				SbiKpiPeriodicity sbiKpiPeriodicity= (SbiKpiPeriodicity) session.load(SbiKpiPeriodicity.class, period.getIdKpiPeriodicity());
				if(sbiKpiPeriodicity!=null){
					hibKpiInstPeriod.setSbiKpiPeriodicity(sbiKpiPeriodicity);
				}
			}



			Transaction tx = session.beginTransaction();			
			session.save(hibKpiInstPeriod);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting kpiInstPeriod value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}





	/**
	 * Insert Periodicity.
	 * 
	 * @param mu the Measure Unit
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertPeriodicity(Periodicity per, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiPeriodicity where idKpiPeriodicity = " + per.getIdKpiPeriodicity());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiKpiPeriodicity hibPer = new SbiKpiPeriodicity();
			hibPer.setIdKpiPeriodicity(per.getIdKpiPeriodicity());
			hibPer.setName(per.getName());
			hibPer.setChronString(per.getCronString());
			hibPer.setDays(per.getDays());
			hibPer.setHours(per.getHours());
			hibPer.setMinutes(per.getMinutes());
			hibPer.setMonths(per.getMonths());
			hibPer.setStartDate(null);
			Transaction tx = session.beginTransaction();			
			session.save(hibPer);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting Periodicity into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}






	/**
	 * Insert ModelResources.
	 * 
	 * @param modRes the modelResource
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertModelResources(ModelResources modRes, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiModelResources where kpiModelResourcesId = " + modRes.getModelResourcesId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiKpiModelResources hibModRes = new SbiKpiModelResources();
			hibModRes.setKpiModelResourcesId(modRes.getModelResourcesId());

			// Model instance should be already inserted

			if (modRes.getModelInstId() != null) {
				Integer modelInstId=modRes.getModelInstId();
				SbiKpiModelInst sbiKpiModInst= (SbiKpiModelInst) session.load(SbiKpiModelInst.class, modelInstId);
				if(sbiKpiModInst!=null){
					hibModRes.setSbiKpiModelInst(sbiKpiModInst);
				}
			}

			// load resource

			if (modRes.getResourceId() != null) {
				Integer resId=modRes.getResourceId();
				IResourceDAO resDAO=DAOFactory.getResourceDAO();
				Resource res=resDAO.loadResourceById(resId);

				insertResource(res, session);
				SbiResources sbiRes= (SbiResources) session.load(SbiResources.class, res.getId());
				if(sbiRes!=null){
					hibModRes.setSbiResources(sbiRes);
				}
			}


			Transaction tx = session.beginTransaction();			
			session.save(hibModRes);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting model resources value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}




	/**
	 * Insert Resources.
	 * 
	 * @param res the resource
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertResource(Resource res, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiResources where resourceId = " + res.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiResources hibRes = new SbiResources();

			hibRes.setResourceId(res.getId());
			hibRes.setResourceCode(res.getCode());
			hibRes.setResourceName(res.getName());
			hibRes.setResourceDescr(res.getDescr());
			hibRes.setColumnName(res.getColumn_name());
			hibRes.setTableName(res.getTable_name());

			//sbi Domains
			if(res.getType()!=null){
				SbiDomains type=(SbiDomains)session.load(SbiDomains.class, res.getTypeId());			
				hibRes.setType(type);
			}

			Transaction tx = session.beginTransaction();			
			session.save(hibRes);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting resource into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	public List getBiObjectToInsert() {
		return biObjectToInsert;
	}

	public void setBiObjectToInsert(List biObjectToInsert) {
		this.biObjectToInsert = biObjectToInsert;
	}




	/**
	 * Insert Alarm.
	 * 
	 * @param res the Alarm
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertAlarm(Alarm alarm, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiAlarm where id = " + alarm.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiAlarm hibAlarm = new SbiAlarm();

			hibAlarm.setId(alarm.getId());
			hibAlarm.setDescr(alarm.getDescr());
			hibAlarm.setLabel(alarm.getLabel());
			hibAlarm.setName(alarm.getName());
			hibAlarm.setText(alarm.getText());
			hibAlarm.setUrl(alarm.getUrl()); 
			hibAlarm.setAutoDisabled(alarm.isAutoDisabled()); 
			hibAlarm.setSingleEvent(alarm.isSingleEvent());

			// kpi Instance (already inserted)
			if(alarm.getIdKpiInstance()!=null){
				SbiKpiInstance sbiKpiInst=(SbiKpiInstance)session.load(SbiKpiInstance.class, alarm.getIdKpiInstance());			
				hibAlarm.setSbiKpiInstance(sbiKpiInst);
			}

			// Threshold Value (already inserted)
			if(alarm.getIdThresholdValue()!=null){
				SbiThresholdValue sbiThValue=(SbiThresholdValue)session.load(SbiThresholdValue.class, alarm.getIdThresholdValue());			
				hibAlarm.setSbiThresholdValue(sbiThValue);
			}

			// SbiDomains modality

			if(alarm.getModalityId()!=null){
				SbiDomains modality=(SbiDomains)session.load(SbiDomains.class, alarm.getModalityId());			
				hibAlarm.setModality(modality);
			}

			// insert all the contacts
			Set<SbiAlarmContact> listSbiContacts = new HashSet<SbiAlarmContact>();
			if(alarm.getSbiAlarmContacts()!=null){
				for (Iterator iterator = alarm.getSbiAlarmContacts().iterator(); iterator.hasNext();) {
					AlarmContact alarmContact = (AlarmContact) iterator.next();
					insertAlarmContact(alarmContact, session);
					SbiAlarmContact sbiAlCon=(SbiAlarmContact)session.load(SbiAlarmContact.class, alarmContact.getId());			
					listSbiContacts.add(sbiAlCon);
				}
			}
			hibAlarm.setSbiAlarmContacts(listSbiContacts);	



			Transaction tx = session.beginTransaction();			
			session.save(hibAlarm);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting alarm into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}











	/**
	 * Insert Alarm Contact
	 * 
	 * @param con the Alarm Contact
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertAlarmContact(AlarmContact con, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiAlarmContact where id = " + con.getId());
			List hibList = hibQuery.list();
			if(!hibList.isEmpty()) {
				return;
			} 

			// main attributes			
			SbiAlarmContact hibCon = new SbiAlarmContact();

			hibCon.setId(con.getId());
			hibCon.setEmail(con.getEmail());
			hibCon.setMobile(con.getMobile());
			hibCon.setName(con.getName());
			hibCon.setResources(con.getResources());

			Transaction tx = session.beginTransaction();			
			session.save(hibCon);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting alarm contact into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}







	/**
	 * Insert ModelAttr.
	 * 
	 * @param modAttr the modelAttribute
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertAllKpiModelAttr(Session session) throws EMFUserError {
		logger.debug("IN");
		try {

			List attrs = DAOFactory.getSbiKpiModelAttrDAO().loadAllModelAttrs();

			for (Iterator iterator = attrs.iterator(); iterator.hasNext();) {
				ModelAttribute modAttribute = (ModelAttribute) iterator.next();

				// if already present not insert again!!
				Query hibQuery = session.createQuery(" from SbiKpiModelAttr where kpiModelAttrId = " + modAttribute.getId());
				List hibList = hibQuery.list();
				if(!hibList.isEmpty()) {
					continue;
				} 


				// main attributes			
				SbiKpiModelAttr hibModAttr = new SbiKpiModelAttr();
				hibModAttr.setKpiModelAttrId(modAttribute.getId());
				hibModAttr.setKpiModelAttrCd(modAttribute.getCode());
				hibModAttr.setKpiModelAttrNm(modAttribute.getName());
				hibModAttr.setKpiModelAttrDescr(modAttribute.getDescr());

				SbiDomains sbiDomains = (SbiDomains)session.load(SbiDomains.class, modAttribute.getTypeId());
				hibModAttr.setSbiDomains(sbiDomains);


				Transaction tx = session.beginTransaction();			
				session.save(hibModAttr);
				tx.commit();

			}

		} catch (Exception e) {
			logger.error("Error while inserting model attr into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}


	/**
	 * Insert ModelAttrVal.
	 * 
	 * @param modAttrval the modelAttribute
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertKpiModelAttrVal(ModelAttribute modAttribute, Integer modelId, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiKpiModelAttrVal s where s.sbiKpiModelAttr.kpiModelAttrId = " + modAttribute.getId() + " AND s.sbiKpiModel.kpiModelId = " + modelId);
			List hibRes = hibQuery.list();
			if(!hibRes.isEmpty()) {
				return;
			} 


			// load kpiModelAttributeValue
			ModelAttributeValue modelAttributeValue = DAOFactory.getSbiKpiModelAttrValDAO().loadModelAttrValByAttrIdAndModelId(modAttribute.getId(), modelId);

			if(modelAttributeValue == null )
				return;

			// main attributes		
			SbiKpiModelAttrVal hibModAttrVal = new SbiKpiModelAttrVal();
			hibModAttrVal.setKpiModelAttrValId(modelAttributeValue.getId());
			hibModAttrVal.setValue(modelAttributeValue.getValue());

			SbiKpiModel sbiKpiModel = (SbiKpiModel)session.load(SbiKpiModel.class, modelAttributeValue.getModelId());
			hibModAttrVal.setSbiKpiModel(sbiKpiModel);

			SbiKpiModelAttr sbiKpiModelAttr = (SbiKpiModelAttr)session.load(SbiKpiModelAttr.class, modelAttributeValue.getAttrId());
			hibModAttrVal.setSbiKpiModelAttr(sbiKpiModelAttr);

			Transaction tx = session.beginTransaction();			
			session.save(hibModAttrVal);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error while inserting model attr value into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}



	/**
	 * Insert BinContent.
	 * 
	 * @param id and content 
	 * @param session the session
	 * 
	 * @throws EMFUserError the EMF user error
	 */
	public void insertBinContet(Integer idContent, byte[] content, Session session) throws EMFUserError {
		logger.debug("IN");
		try {
			Query hibQuery = session.createQuery(" from SbiBinContents s where s.id = " + idContent);
			List hibRes = hibQuery.list();
			if(!hibRes.isEmpty()) {
				return;
			} 

			SbiBinContents hibContent = new SbiBinContents();
			hibContent.setId(idContent);
			hibContent.setContent(content);
			Transaction tx = session.beginTransaction();			
			session.save(hibContent);
			tx.commit();

		} catch (Exception e) {
			logger.error("Error while inserting binContent into export database " , e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, "8005", "component_impexp_messages");
		}finally{
			logger.debug("OUT");
		}
	}




}

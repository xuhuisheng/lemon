package com.mossle.modeler.rs;

import java.util.*;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import com.mossle.core.mapper.JsonMapper;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("modeler")
public class ModelerResource {
    private static Logger logger = LoggerFactory
            .getLogger(ModelerResource.class);
    private ProcessEngine processEngine;
    private JsonMapper jsonMapper = new JsonMapper();

    @GET
    @Path("open")
    @Produces(MediaType.APPLICATION_JSON)
    public Object open(@QueryParam("id") String modelId) throws Exception {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        Model model = repositoryService.getModel(modelId);

        if (model == null) {
            logger.info("model({}) is null", modelId);
            model = repositoryService.newModel();
            repositoryService.saveModel(model);
        }

        Map root = new HashMap();
        root.put("modelId", model.getId());
        root.put("name", "name");
        root.put("revision", 1);
        root.put("description", "description");

        byte[] bytes = repositoryService.getModelEditorSource(model.getId());

        if (bytes != null) {
            String modelEditorSource = new String(bytes, "utf-8");
            logger.info("modelEditorSource : {}", modelEditorSource);

            Map modelNode = jsonMapper.fromJson(modelEditorSource, Map.class);
            root.put("model", modelNode);
        } else {
            Map modelNode = new HashMap();
            modelNode.put("id", "canvas");
            modelNode.put("resourceId", "canvas");

            Map stencilSetNode = new HashMap();
            stencilSetNode.put("namespace",
                    "http://b3mn.org/stencilset/bpmn2.0#");
            modelNode.put("stencilset", stencilSetNode);

            model.setMetaInfo(jsonMapper.toJson(root));
            model.setName("name");
            model.setKey("key");

            root.put("model", modelNode);
        }

        logger.info("model : {}", root);

        return root;
    }

    @PUT
    @Path("save")
    @Produces(MediaType.APPLICATION_JSON)
    public Map save(@FormParam("glossary_xml") String glossayXml,
            @FormParam("id") String id,
            @FormParam("description") String description,
            @FormParam("json_xml") String jsonXml,
            @FormParam("name") String name,
            @FormParam("namespace") String namespace,
            @FormParam("parent") String parent,
            @FormParam("svg_xml") String svgXml,
            @FormParam("type") String type, @FormParam("views") String views)
            throws Exception {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        Model model = repositoryService.getModel(id);
        model.setName(name);
        // model.setMetaInfo(root.toString());
        System.out.println("jsonXml : " + jsonXml);
        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(model.getId(),
                jsonXml.getBytes("utf-8"));

        Map map = new HashMap();

        return map;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}

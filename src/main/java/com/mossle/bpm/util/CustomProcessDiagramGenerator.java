package com.mossle.bpm.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.BpmnModel;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;

import org.apache.commons.io.FilenameUtils;

/**
 * 流程图绘制工具
 */
public class CustomProcessDiagramGenerator {
    private static List<String> taskType = new ArrayList<String>();
    private static List<String> eventType = new ArrayList<String>();
    private static List<String> gatewayType = new ArrayList<String>();
    private static List<String> subProcessType = new ArrayList<String>();
    private static Color RUNNING_COLOR = Color.RED;
    private static Color HISTORY_COLOR = Color.GREEN;
    private static Stroke THICK_BORDER_STROKE = new BasicStroke(3.0f);
    private int minX;
    private int minY;

    public CustomProcessDiagramGenerator() {
        init();
    }

    protected static void init() {
        taskType.add(BpmnXMLConstants.ELEMENT_TASK_MANUAL);
        taskType.add(BpmnXMLConstants.ELEMENT_TASK_RECEIVE);
        taskType.add(BpmnXMLConstants.ELEMENT_TASK_SCRIPT);
        taskType.add(BpmnXMLConstants.ELEMENT_TASK_SEND);
        taskType.add(BpmnXMLConstants.ELEMENT_TASK_SERVICE);
        taskType.add(BpmnXMLConstants.ELEMENT_TASK_USER);

        gatewayType.add(BpmnXMLConstants.ELEMENT_GATEWAY_EXCLUSIVE);
        gatewayType.add(BpmnXMLConstants.ELEMENT_GATEWAY_INCLUSIVE);
        gatewayType.add(BpmnXMLConstants.ELEMENT_GATEWAY_EVENT);
        gatewayType.add(BpmnXMLConstants.ELEMENT_GATEWAY_PARALLEL);

        eventType.add("intermediateTimer");
        eventType.add("intermediateMessageCatch");
        eventType.add("intermediateSignalCatch");
        eventType.add("intermediateSignalThrow");
        eventType.add("messageStartEvent");
        eventType.add("startTimerEvent");
        eventType.add(BpmnXMLConstants.ELEMENT_ERROR);
        eventType.add(BpmnXMLConstants.ELEMENT_EVENT_START);
        eventType.add("errorEndEvent");
        eventType.add(BpmnXMLConstants.ELEMENT_EVENT_END);

        subProcessType.add(BpmnXMLConstants.ELEMENT_SUBPROCESS);
        subProcessType.add(BpmnXMLConstants.ELEMENT_CALL_ACTIVITY);
    }

    public InputStream generateDiagram(String processInstanceId)
            throws IOException {
        HistoricProcessInstance historicProcessInstance = Context
                .getCommandContext().getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(processInstanceId);
        String processDefinitionId = historicProcessInstance
                .getProcessDefinitionId();
        GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(
                processDefinitionId);
        BpmnModel bpmnModel = getBpmnModelCmd.execute(Context
                .getCommandContext());
        Point point = getMinXAndMinY(bpmnModel);
        this.minX = point.x;
        this.minY = point.y;
        this.minX = (this.minX <= 5) ? 5 : this.minX;
        this.minY = (this.minY <= 5) ? 5 : this.minY;
        this.minX -= 5;
        this.minY -= 5;

        ProcessDefinitionEntity definition = Context
                .getProcessEngineConfiguration().getProcessDefinitionCache()
                .get(processDefinitionId);
        String diagramResourceName = definition.getDiagramResourceName();
        String deploymentId = definition.getDeploymentId();
        byte[] bytes = Context
                .getCommandContext()
                .getResourceEntityManager()
                .findResourceByDeploymentIdAndResourceName(deploymentId,
                        diagramResourceName).getBytes();
        InputStream originDiagram = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(originDiagram);

        HistoricActivityInstanceQueryImpl historicActivityInstanceQueryImpl = new HistoricActivityInstanceQueryImpl();
        historicActivityInstanceQueryImpl.processInstanceId(processInstanceId);

        Page page = new Page(0, 100);
        List<HistoricActivityInstance> activityInstances = Context
                .getCommandContext()
                .getHistoricActivityInstanceEntityManager()
                .findHistoricActivityInstancesByQueryCriteria(
                        historicActivityInstanceQueryImpl, page);

        for (HistoricActivityInstance historicActivityInstance : activityInstances) {
            String historicActivityId = historicActivityInstance
                    .getActivityId();
            ActivityImpl activity = definition.findActivity(historicActivityId);

            if (activity != null) {
                if (historicActivityInstance.getEndTime() == null) { // 节点正在运行中
                    signRunningNode(
                            image, //
                            activity.getX() - this.minX, activity.getY()
                                    - this.minY, activity.getWidth(),
                            activity.getHeight(),
                            historicActivityInstance.getActivityType());
                } else { // 节点已经结束
                    signHistoryNode(
                            image, //
                            activity.getX() - this.minX, activity.getY()
                                    - this.minY, activity.getWidth(),
                            activity.getHeight(),
                            historicActivityInstance.getActivityType());
                }
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String formatName = getDiagramExtension(diagramResourceName);
        ImageIO.write(image, formatName, out);

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static String getDiagramExtension(String diagramResourceName) {
        return FilenameUtils.getExtension(diagramResourceName);
    }

    /**
     * 标记运行节点
     * 
     * @param image
     *            原始图片
     * @param x
     *            左上角节点坐在X位置
     * @param y
     *            左上角节点坐在Y位置
     * @param width
     *            宽
     * @param height
     *            高
     * @param activityType
     *            节点类型
     */
    private static void signRunningNode(BufferedImage image, int x, int y,
            int width, int height, String activityType) {
        Color nodeColor = RUNNING_COLOR;
        Graphics2D graphics = image.createGraphics();

        try {
            drawNodeBorder(x, y, width, height, graphics, nodeColor,
                    activityType);
        } finally {
            graphics.dispose();
        }
    }

    /**
     * 标记历史节点
     * 
     * @param image
     *            原始图片
     * @param x
     *            左上角节点坐在X位置
     * @param y
     *            左上角节点坐在Y位置
     * @param width
     *            宽
     * @param height
     *            高
     * @param activityType
     *            节点类型
     */
    private static void signHistoryNode(BufferedImage image, int x, int y,
            int width, int height, String activityType) {
        Color nodeColor = HISTORY_COLOR;
        Graphics2D graphics = image.createGraphics();

        try {
            drawNodeBorder(x, y, width, height, graphics, nodeColor,
                    activityType);
        } finally {
            graphics.dispose();
        }
    }

    /**
     * 绘制节点边框
     * 
     * @param x
     *            左上角节点坐在X位置
     * @param y
     *            左上角节点坐在Y位置
     * @param width
     *            宽
     * @param height
     *            高
     * @param graphics
     *            绘图对象
     * @param color
     *            节点边框颜色
     * @param activityType
     *            节点类型
     */
    protected static void drawNodeBorder(int x, int y, int width, int height,
            Graphics2D graphics, Color color, String activityType) {
        graphics.setPaint(color);
        graphics.setStroke(THICK_BORDER_STROKE);

        if (taskType.contains(activityType)) {
            drawTask(x, y, width, height, graphics);
        } else if (gatewayType.contains(activityType)) {
            drawGateway(x, y, width, height, graphics);
        } else if (eventType.contains(activityType)) {
            drawEvent(x, y, width, height, graphics);
        } else if (subProcessType.contains(activityType)) {
            drawSubProcess(x, y, width, height, graphics);
        }
    }

    /**
     * 绘制任务
     */
    protected static void drawTask(int x, int y, int width, int height,
            Graphics2D graphics) {
        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width,
                height, 20, 20);
        graphics.draw(rect);
    }

    /**
     * 绘制网关
     */
    protected static void drawGateway(int x, int y, int width, int height,
            Graphics2D graphics) {
        Polygon rhombus = new Polygon();
        rhombus.addPoint(x, y + (height / 2));
        rhombus.addPoint(x + (width / 2), y + height);
        rhombus.addPoint(x + width, y + (height / 2));
        rhombus.addPoint(x + (width / 2), y);
        graphics.draw(rhombus);
    }

    /**
     * 绘制任务
     */
    protected static void drawEvent(int x, int y, int width, int height,
            Graphics2D graphics) {
        Double circle = new Ellipse2D.Double(x, y, width, height);
        graphics.draw(circle);
    }

    /**
     * 绘制子流程
     */
    protected static void drawSubProcess(int x, int y, int width, int height,
            Graphics2D graphics) {
        RoundRectangle2D rect = new RoundRectangle2D.Double(x + 1, y + 1,
                width - 2, height - 2, 5, 5);
        graphics.draw(rect);
    }

    protected Point getMinXAndMinY(BpmnModel bpmnModel) {
        // We need to calculate maximum values to know how big the image will be in its entirety
        double minX = java.lang.Double.MAX_VALUE;
        double maxX = 0;
        double minY = java.lang.Double.MAX_VALUE;
        double maxY = 0;

        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            minX = graphicInfo.getX();
            maxX = graphicInfo.getX() + graphicInfo.getWidth();
            minY = graphicInfo.getY();
            maxY = graphicInfo.getY() + graphicInfo.getHeight();
        }

        List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);

        for (FlowNode flowNode : flowNodes) {
            GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode
                    .getId());

            // width
            if ((flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth()) > maxX) {
                maxX = flowNodeGraphicInfo.getX()
                        + flowNodeGraphicInfo.getWidth();
            }

            if (flowNodeGraphicInfo.getX() < minX) {
                minX = flowNodeGraphicInfo.getX();
            }

            // height
            if ((flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight()) > maxY) {
                maxY = flowNodeGraphicInfo.getY()
                        + flowNodeGraphicInfo.getHeight();
            }

            if (flowNodeGraphicInfo.getY() < minY) {
                minY = flowNodeGraphicInfo.getY();
            }

            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
                List<GraphicInfo> graphicInfoList = bpmnModel
                        .getFlowLocationGraphicInfo(sequenceFlow.getId());

                for (GraphicInfo graphicInfo : graphicInfoList) {
                    // width
                    if (graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }

                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }

                    // height
                    if (graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }

                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        List<Artifact> artifacts = gatherAllArtifacts(bpmnModel);

        for (Artifact artifact : artifacts) {
            GraphicInfo artifactGraphicInfo = bpmnModel.getGraphicInfo(artifact
                    .getId());

            if (artifactGraphicInfo != null) {
                // width
                if ((artifactGraphicInfo.getX() + artifactGraphicInfo
                        .getWidth()) > maxX) {
                    maxX = artifactGraphicInfo.getX()
                            + artifactGraphicInfo.getWidth();
                }

                if (artifactGraphicInfo.getX() < minX) {
                    minX = artifactGraphicInfo.getX();
                }

                // height
                if ((artifactGraphicInfo.getY() + artifactGraphicInfo
                        .getHeight()) > maxY) {
                    maxY = artifactGraphicInfo.getY()
                            + artifactGraphicInfo.getHeight();
                }

                if (artifactGraphicInfo.getY() < minY) {
                    minY = artifactGraphicInfo.getY();
                }
            }

            List<GraphicInfo> graphicInfoList = bpmnModel
                    .getFlowLocationGraphicInfo(artifact.getId());

            if (graphicInfoList != null) {
                for (GraphicInfo graphicInfo : graphicInfoList) {
                    // width
                    if (graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }

                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }

                    // height
                    if (graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }

                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        int nrOfLanes = 0;

        for (org.activiti.bpmn.model.Process process : bpmnModel.getProcesses()) {
            for (Lane l : process.getLanes()) {
                nrOfLanes++;

                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(l.getId());

                // // width
                if ((graphicInfo.getX() + graphicInfo.getWidth()) > maxX) {
                    maxX = graphicInfo.getX() + graphicInfo.getWidth();
                }

                if (graphicInfo.getX() < minX) {
                    minX = graphicInfo.getX();
                }

                // height
                if ((graphicInfo.getY() + graphicInfo.getHeight()) > maxY) {
                    maxY = graphicInfo.getY() + graphicInfo.getHeight();
                }

                if (graphicInfo.getY() < minY) {
                    minY = graphicInfo.getY();
                }
            }
        }

        // Special case, see http://jira.codehaus.org/browse/ACT-1431
        if ((flowNodes.size() == 0) && (bpmnModel.getPools().size() == 0)
                && (nrOfLanes == 0)) {
            // Nothing to show
            minX = 0;
            minY = 0;
        }

        return new Point((int) minX, (int) minY);
    }

    protected static List<Artifact> gatherAllArtifacts(BpmnModel bpmnModel) {
        List<Artifact> artifacts = new ArrayList<Artifact>();

        for (org.activiti.bpmn.model.Process process : bpmnModel.getProcesses()) {
            artifacts.addAll(process.getArtifacts());
        }

        return artifacts;
    }

    protected static List<FlowNode> gatherAllFlowNodes(BpmnModel bpmnModel) {
        List<FlowNode> flowNodes = new ArrayList<FlowNode>();

        for (org.activiti.bpmn.model.Process process : bpmnModel.getProcesses()) {
            flowNodes.addAll(gatherAllFlowNodes(process));
        }

        return flowNodes;
    }

    protected static List<FlowNode> gatherAllFlowNodes(
            FlowElementsContainer flowElementsContainer) {
        List<FlowNode> flowNodes = new ArrayList<FlowNode>();

        for (FlowElement flowElement : flowElementsContainer.getFlowElements()) {
            if (flowElement instanceof FlowNode) {
                flowNodes.add((FlowNode) flowElement);
            }

            if (flowElement instanceof FlowElementsContainer) {
                flowNodes
                        .addAll(gatherAllFlowNodes((FlowElementsContainer) flowElement));
            }
        }

        return flowNodes;
    }
}

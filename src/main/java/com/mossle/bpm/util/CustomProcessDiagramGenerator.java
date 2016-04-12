package com.mossle.bpm.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.mossle.bpm.graph.ActivitiHistoryGraphBuilder;
import com.mossle.bpm.graph.Edge;
import com.mossle.bpm.graph.Graph;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.SequenceFlow;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.HistoricActivityInstanceQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import org.apache.commons.io.FilenameUtils;

/**
 * 流程图绘制工具
 */
public class CustomProcessDiagramGenerator {
    public static final int OFFSET_SUBPROCESS = 5;
    public static final int OFFSET_TASK = 10;
    private static List<String> taskType = new ArrayList<String>();
    private static List<String> eventType = new ArrayList<String>();
    private static List<String> gatewayType = new ArrayList<String>();
    private static List<String> subProcessType = new ArrayList<String>();
    private static Color RUNNING_COLOR = Color.RED;
    private static Color HISTORY_COLOR = Color.decode("#337ab7");
    private static Color SKIP_COLOR = Color.GRAY;
    private static Stroke THICK_BORDER_STROKE = new BasicStroke(3.0f);
    private int minX = 0;
    private int minY = 0;

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

        // Point point = getMinXAndMinY(bpmnModel);
        // this.minX = point.x;
        // this.minY = point.y;
        // this.minX = (this.minX <= 5) ? 5 : this.minX;
        // this.minY = (this.minY <= 5) ? 5 : this.minY;
        // this.minX -= 5;
        // this.minY -= 5;
        ProcessDefinitionEntity definition = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId).execute(Context.getCommandContext());
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
        historicActivityInstanceQueryImpl.processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc();

        Page page = new Page(0, 100);
        List<HistoricActivityInstance> activityInstances = Context
                .getCommandContext()
                .getHistoricActivityInstanceEntityManager()
                .findHistoricActivityInstancesByQueryCriteria(
                        historicActivityInstanceQueryImpl, page);

        this.drawHistoryFlow(image, processInstanceId);

        for (HistoricActivityInstance historicActivityInstance : activityInstances) {
            String historicActivityId = historicActivityInstance
                    .getActivityId();
            ActivityImpl activity = definition.findActivity(historicActivityId);

            if (activity != null) {
                if (historicActivityInstance.getEndTime() == null) {
                    // 节点正在运行中
                    signRunningNode(image, activity.getX() - this.minX,
                            activity.getY() - this.minY, activity.getWidth(),
                            activity.getHeight(),
                            historicActivityInstance.getActivityType());
                } else {
                    String deleteReason = null;

                    if (historicActivityInstance.getTaskId() != null) {
                        deleteReason = Context
                                .getCommandContext()
                                .getHistoricTaskInstanceEntityManager()
                                .findHistoricTaskInstanceById(
                                        historicActivityInstance.getTaskId())
                                .getDeleteReason();
                    }

                    // 节点已经结束
                    if ("跳过".equals(deleteReason)) {
                        signSkipNode(image, activity.getX() - this.minX,
                                activity.getY() - this.minY,
                                activity.getWidth(), activity.getHeight(),
                                historicActivityInstance.getActivityType());
                    } else {
                        signHistoryNode(image, activity.getX() - this.minX,
                                activity.getY() - this.minY,
                                activity.getWidth(), activity.getHeight(),
                                historicActivityInstance.getActivityType());
                    }
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

    private static void signSkipNode(BufferedImage image, int x, int y,
            int width, int height, String activityType) {
        Color nodeColor = SKIP_COLOR;
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
                height, OFFSET_TASK, OFFSET_TASK);
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
                width - 2, height - 2, OFFSET_SUBPROCESS, OFFSET_SUBPROCESS);
        graphics.draw(rect);
    }

    protected Point getMinXAndMinY(BpmnModel bpmnModel) {
        // We need to calculate maximum values to know how big the image will be in its entirety
        double theMinX = java.lang.Double.MAX_VALUE;
        double theMaxX = 0;
        double theMinY = java.lang.Double.MAX_VALUE;
        double theMaxY = 0;

        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            theMinX = graphicInfo.getX();
            theMaxX = graphicInfo.getX() + graphicInfo.getWidth();
            theMinY = graphicInfo.getY();
            theMaxY = graphicInfo.getY() + graphicInfo.getHeight();
        }

        List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);

        for (FlowNode flowNode : flowNodes) {
            GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode
                    .getId());

            // width
            if ((flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth()) > theMaxX) {
                theMaxX = flowNodeGraphicInfo.getX()
                        + flowNodeGraphicInfo.getWidth();
            }

            if (flowNodeGraphicInfo.getX() < theMinX) {
                theMinX = flowNodeGraphicInfo.getX();
            }

            // height
            if ((flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight()) > theMaxY) {
                theMaxY = flowNodeGraphicInfo.getY()
                        + flowNodeGraphicInfo.getHeight();
            }

            if (flowNodeGraphicInfo.getY() < theMinY) {
                theMinY = flowNodeGraphicInfo.getY();
            }

            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
                List<GraphicInfo> graphicInfoList = bpmnModel
                        .getFlowLocationGraphicInfo(sequenceFlow.getId());

                for (GraphicInfo graphicInfo : graphicInfoList) {
                    // width
                    if (graphicInfo.getX() > theMaxX) {
                        theMaxX = graphicInfo.getX();
                    }

                    if (graphicInfo.getX() < theMinX) {
                        theMinX = graphicInfo.getX();
                    }

                    // height
                    if (graphicInfo.getY() > theMaxY) {
                        theMaxY = graphicInfo.getY();
                    }

                    if (graphicInfo.getY() < theMinY) {
                        theMinY = graphicInfo.getY();
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
                        .getWidth()) > theMaxX) {
                    theMaxX = artifactGraphicInfo.getX()
                            + artifactGraphicInfo.getWidth();
                }

                if (artifactGraphicInfo.getX() < theMinX) {
                    theMinX = artifactGraphicInfo.getX();
                }

                // height
                if ((artifactGraphicInfo.getY() + artifactGraphicInfo
                        .getHeight()) > theMaxY) {
                    theMaxY = artifactGraphicInfo.getY()
                            + artifactGraphicInfo.getHeight();
                }

                if (artifactGraphicInfo.getY() < theMinY) {
                    theMinY = artifactGraphicInfo.getY();
                }
            }

            List<GraphicInfo> graphicInfoList = bpmnModel
                    .getFlowLocationGraphicInfo(artifact.getId());

            if (graphicInfoList != null) {
                for (GraphicInfo graphicInfo : graphicInfoList) {
                    // width
                    if (graphicInfo.getX() > theMaxX) {
                        theMaxX = graphicInfo.getX();
                    }

                    if (graphicInfo.getX() < theMinX) {
                        theMinX = graphicInfo.getX();
                    }

                    // height
                    if (graphicInfo.getY() > theMaxY) {
                        theMaxY = graphicInfo.getY();
                    }

                    if (graphicInfo.getY() < theMinY) {
                        theMinY = graphicInfo.getY();
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
                if ((graphicInfo.getX() + graphicInfo.getWidth()) > theMaxX) {
                    theMaxX = graphicInfo.getX() + graphicInfo.getWidth();
                }

                if (graphicInfo.getX() < theMinX) {
                    theMinX = graphicInfo.getX();
                }

                // height
                if ((graphicInfo.getY() + graphicInfo.getHeight()) > theMaxY) {
                    theMaxY = graphicInfo.getY() + graphicInfo.getHeight();
                }

                if (graphicInfo.getY() < theMinY) {
                    theMinY = graphicInfo.getY();
                }
            }
        }

        // Special case, see http://jira.codehaus.org/browse/ACT-1431
        if ((flowNodes.size() == 0) && (bpmnModel.getPools().size() == 0)
                && (nrOfLanes == 0)) {
            // Nothing to show
            theMinX = 0;
            theMinY = 0;
        }

        return new Point((int) theMinX, (int) theMinY);
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

    public void drawHistoryFlow(BufferedImage image, String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = Context
                .getCommandContext().getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(processInstanceId);
        String processDefinitionId = historicProcessInstance
                .getProcessDefinitionId();
        Graph graph = new ActivitiHistoryGraphBuilder(processInstanceId)
                .build();

        for (Edge edge : graph.getEdges()) {
            drawSequenceFlow(image, processDefinitionId, edge.getName());
        }
    }

    public void drawSequenceFlow(BufferedImage image,
            String processDefinitionId, String sequenceFlowId) {
        GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(
                processDefinitionId);
        BpmnModel bpmnModel = getBpmnModelCmd.execute(Context
                .getCommandContext());

        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(HISTORY_COLOR);
        graphics.setStroke(new BasicStroke(2f));

        try {
            List<GraphicInfo> graphicInfoList = bpmnModel
                    .getFlowLocationGraphicInfo(sequenceFlowId);

            int[] xPoints = new int[graphicInfoList.size()];
            int[] yPoints = new int[graphicInfoList.size()];

            for (int i = 1; i < graphicInfoList.size(); i++) {
                GraphicInfo graphicInfo = graphicInfoList.get(i);
                GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

                if (i == 1) {
                    xPoints[0] = (int) previousGraphicInfo.getX() - minX;
                    yPoints[0] = (int) previousGraphicInfo.getY() - minY;
                }

                xPoints[i] = (int) graphicInfo.getX() - minX;
                yPoints[i] = (int) graphicInfo.getY() - minY;
            }

            int radius = 15;

            Path2D path = new Path2D.Double();

            for (int i = 0; i < xPoints.length; i++) {
                Integer anchorX = xPoints[i];
                Integer anchorY = yPoints[i];

                double targetX = anchorX;
                double targetY = anchorY;

                double ax = 0;
                double ay = 0;
                double bx = 0;
                double by = 0;
                double zx = 0;
                double zy = 0;

                if ((i > 0) && (i < (xPoints.length - 1))) {
                    Integer cx = anchorX;
                    Integer cy = anchorY;

                    // pivot point of prev line
                    double lineLengthY = yPoints[i] - yPoints[i - 1];

                    // pivot point of prev line
                    double lineLengthX = xPoints[i] - xPoints[i - 1];
                    double lineLength = Math.sqrt(Math.pow(lineLengthY, 2)
                            + Math.pow(lineLengthX, 2));
                    double dx = (lineLengthX * radius) / lineLength;
                    double dy = (lineLengthY * radius) / lineLength;
                    targetX = targetX - dx;
                    targetY = targetY - dy;

                    // isDefaultConditionAvailable = isDefault && i == 1 && lineLength > 10;
                    if ((lineLength < (2 * radius)) && (i > 1)) {
                        targetX = xPoints[i] - (lineLengthX / 2);
                        targetY = yPoints[i] - (lineLengthY / 2);
                    }

                    // pivot point of next line
                    lineLengthY = yPoints[i + 1] - yPoints[i];
                    lineLengthX = xPoints[i + 1] - xPoints[i];
                    lineLength = Math.sqrt(Math.pow(lineLengthY, 2)
                            + Math.pow(lineLengthX, 2));

                    if (lineLength < radius) {
                        lineLength = radius;
                    }

                    dx = (lineLengthX * radius) / lineLength;
                    dy = (lineLengthY * radius) / lineLength;

                    double nextSrcX = xPoints[i] + dx;
                    double nextSrcY = yPoints[i] + dy;

                    if ((lineLength < (2 * radius))
                            && (i < (xPoints.length - 2))) {
                        nextSrcX = xPoints[i] + (lineLengthX / 2);
                        nextSrcY = yPoints[i] + (lineLengthY / 2);
                    }

                    double dx0 = (cx - targetX) / 3;
                    double dy0 = (cy - targetY) / 3;
                    ax = cx - dx0;
                    ay = cy - dy0;

                    double dx1 = (cx - nextSrcX) / 3;
                    double dy1 = (cy - nextSrcY) / 3;
                    bx = cx - dx1;
                    by = cy - dy1;

                    zx = nextSrcX;
                    zy = nextSrcY;
                }

                if (i == 0) {
                    path.moveTo(targetX, targetY);
                } else {
                    path.lineTo(targetX, targetY);
                }

                if ((i > 0) && (i < (xPoints.length - 1))) {
                    // add curve
                    path.curveTo(ax, ay, bx, by, zx, zy);
                }
            }

            graphics.draw(path);

            // draw arrow
            Line2D.Double line = new Line2D.Double(xPoints[xPoints.length - 2],
                    yPoints[xPoints.length - 2], xPoints[xPoints.length - 1],
                    yPoints[xPoints.length - 1]);

            int ARROW_WIDTH = 5;
            int doubleArrowWidth = 2 * ARROW_WIDTH;
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(0, 0);
            arrowHead.addPoint(-ARROW_WIDTH, -doubleArrowWidth);
            arrowHead.addPoint(ARROW_WIDTH, -doubleArrowWidth);

            AffineTransform transformation = new AffineTransform();
            transformation.setToIdentity();

            double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
            transformation.translate(line.x2, line.y2);
            transformation.rotate((angle - (Math.PI / 2d)));

            AffineTransform originalTransformation = graphics.getTransform();
            graphics.setTransform(transformation);
            graphics.fill(arrowHead);
            graphics.setTransform(originalTransformation);
        } finally {
            graphics.dispose();
        }
    }
}

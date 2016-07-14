package com.mossle.bpm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.bpm.support.EdgeDTO;
import com.mossle.bpm.support.NodeDTO;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class TraceService {
    private static Logger logger = LoggerFactory.getLogger(TraceService.class);
    private ProcessEngine processEngine;

    public List<NodeDTO> traceProcessInstance(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = processEngine
                .getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(
                historicProcessInstance.getProcessDefinitionId());
        BpmnModel bpmnModel = processEngine.getManagementService()
                .executeCommand(getBpmnModelCmd);

        Map<String, GraphicInfo> graphicInfoMap = bpmnModel.getLocationMap();

        List<NodeDTO> nodeDtos = new ArrayList<NodeDTO>();

        for (Map.Entry<String, GraphicInfo> entry : graphicInfoMap.entrySet()) {
            String key = entry.getKey();
            GraphicInfo graphicInfo = entry.getValue();
            nodeDtos.add(this.convertNodeDto(graphicInfo,
                    bpmnModel.getFlowElement(key), key, bpmnModel));
        }

        return nodeDtos;
    }

    public NodeDTO convertNodeDto(GraphicInfo graphicInfo,
            FlowElement flowElement, String id, BpmnModel bpmnModel) {
        NodeDTO nodeDto = new NodeDTO();
        nodeDto.setX((int) graphicInfo.getX());
        nodeDto.setY((int) graphicInfo.getY());
        nodeDto.setWidth((int) graphicInfo.getWidth());
        nodeDto.setHeight((int) graphicInfo.getHeight());
        //
        nodeDto.setId(id);
        nodeDto.setName(flowElement.getName());

        if (flowElement instanceof UserTask) {
            nodeDto.setType("用户任务");

            UserTask userTask = (UserTask) flowElement;
            nodeDto.setAssignee(userTask.getAssignee());
        } else if (flowElement instanceof StartEvent) {
            nodeDto.setType("开始事件");
        } else if (flowElement instanceof EndEvent) {
            nodeDto.setType("结束事件");
        } else if (flowElement instanceof ExclusiveGateway) {
            nodeDto.setType("选择网关");
        }

        if (flowElement instanceof FlowNode) {
            FlowNode flowNode = (FlowNode) flowElement;

            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
                EdgeDTO edgeDto = new EdgeDTO();
                edgeDto.setId(sequenceFlow.getTargetRef());

                for (GraphicInfo flowGraphicInfo : bpmnModel
                        .getFlowLocationGraphicInfo(sequenceFlow.getId())) {
                    List<Integer> position = new ArrayList<Integer>();
                    position.add((int) flowGraphicInfo.getX()
                            - ((int) graphicInfo.getWidth() / 2));
                    position.add((int) flowGraphicInfo.getY()
                            - ((int) graphicInfo.getHeight() / 2));
                    edgeDto.getG().add(position);
                }

                edgeDto.getG().remove(0);
                edgeDto.getG().remove(edgeDto.getG().size() - 1);
                logger.debug("{}", edgeDto.getG());
                nodeDto.getOutgoings().add(edgeDto);
            }
        }

        return nodeDto;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}

package com.mossle.bpm.graph;

import java.util.HashSet;
import java.util.Set;

import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;

/**
 * 根据流程定义，构建设计阶段的图.
 */
public class ActivitiGraphBuilder {
    /**
     * 流程定义id.
     */
    private String processDefinitionId;

    /**
     * 流程定义.
     */
    private ProcessDefinitionEntity processDefinitionEntity;

    /**
     * 已访问的节点id.
     */
    private Set<String> visitedNodeIds = new HashSet<String>();

    /**
     * 构造方法.
     */
    public ActivitiGraphBuilder(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    /**
     * 构建图.
     */
    public Graph build() {
        this.fetchProcessDefinitionEntity();

        Node initial = visitNode(processDefinitionEntity.getInitial());

        Graph graph = new Graph();
        graph.setInitial(initial);

        return graph;
    }

    /**
     * 获取流程定义.
     */
    public void fetchProcessDefinitionEntity() {
        GetDeploymentProcessDefinitionCmd cmd = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId);
        processDefinitionEntity = cmd.execute(Context.getCommandContext());
    }

    /**
     * 遍历.
     */
    public Node visitNode(PvmActivity pvmActivity) {
        if (visitedNodeIds.contains(pvmActivity.getId())) {
            return null;
        }

        visitedNodeIds.add(pvmActivity.getId());

        Node currentNode = new Node();
        currentNode.setId(pvmActivity.getId());
        currentNode.setName(this.getString(pvmActivity.getProperty("name")));
        currentNode.setType(this.getString(pvmActivity.getProperty("type")));

        for (PvmTransition pvmTransition : pvmActivity.getOutgoingTransitions()) {
            PvmActivity destination = pvmTransition.getDestination();
            Node targetNode = this.visitNode(destination);

            if (targetNode == null) {
                continue;
            }

            Edge edge = new Edge();
            edge.setId(pvmTransition.getId());
            edge.setSrc(currentNode);
            edge.setDest(targetNode);
            currentNode.getOutgoingEdges().add(edge);
        }

        return currentNode;
    }

    /**
     * 把object转换为string.
     */
    public String getString(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String) object;
        } else {
            return object.toString();
        }
    }
}

package com.mossle.bpm.graph;

import java.util.HashSet;
import java.util.Set;

import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class ActivitiGraphBuilder {
    private String processDefinitionId;
    private ProcessDefinitionEntity processDefinitionEntity;
    private Set<String> visitedNodeIds = new HashSet<String>();

    public ActivitiGraphBuilder(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public Graph build() {
        this.fetchProcessDefinitionEntity();

        Node initial = visitNode(processDefinitionEntity.getInitial());

        Graph graph = new Graph();
        graph.setInitial(initial);

        return graph;
    }

    public void fetchProcessDefinitionEntity() {
        GetDeploymentProcessDefinitionCmd cmd = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId);
        processDefinitionEntity = cmd.execute(Context.getCommandContext());
    }

    public Node visitNode(PvmActivity pvmActivity) {
        if (visitedNodeIds.contains(pvmActivity.getId())) {
            return null;
        }

        visitedNodeIds.add(pvmActivity.getId());

        Node currentNode = new Node();
        currentNode.setId(pvmActivity.getId());
        currentNode.setName(getString(pvmActivity.getProperty("name")));
        currentNode.setType(getString(pvmActivity.getProperty("type")));

        for (PvmTransition pvmTransition : pvmActivity.getOutgoingTransitions()) {
            PvmActivity destination = pvmTransition.getDestination();
            Node targetNode = visitNode(destination);

            if (targetNode == null) {
                continue;
            }

            Edge edge = new Edge();
            edge.setId(pvmTransition.getId());
            edge.setSrc(currentNode);
            edge.setDest(targetNode);
            currentNode.getEdges().add(edge);
        }

        return currentNode;
    }

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

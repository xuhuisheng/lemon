package com.mossle.bpm.cmd;


import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

public class JumpCmd implements Command<Object> {
	private String activityId;
	private String processInstanceId;
	private String jumpOrigin;

	public JumpCmd(String processInstanceId, String activityId) {
		this(processInstanceId,activityId,"jump");
	}
	public JumpCmd(String processInstanceId, String activityId , String jumpOrigin) {
		this.activityId = activityId;
		this.processInstanceId = processInstanceId;
		this.jumpOrigin = jumpOrigin;
	}

	public Object execute(CommandContext commandContext) {
        //parent execution
		ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(processInstanceId);

        //1. 删除当前execution的所有子execution
        //2. 删除当前execution下的所有task
        //3. 删除当前execution下的所有job
        executionEntity.destroyScope(jumpOrigin);

		ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
		ActivityImpl activity = processDefinition.findActivity(activityId);

		executionEntity.executeActivity(activity);


		return null;
	}
}

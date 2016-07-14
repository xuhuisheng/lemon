
TaskOperation = function(conf) {
	if (!conf) {
		conf = {
			formId: 'xform',
			toolbarId: 'xformToolbar'
		};
	}

	this.formId = conf.formId;
	this.toolbarId = conf.toolbarId;
};

TaskOperation.prototype.saveDraft = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/process-operation-saveDraft.do');
	$('#' + this.formId).submit();
};

TaskOperation.prototype.startProcess = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/process-operation-startProcess.do');
	$('#' + this.formId).submit();
};

TaskOperation.prototype.taskConf = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/process-operation-taskConf.do');
	$('#' + this.formId).submit();
};

TaskOperation.prototype.confirmStartProcess = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/process-operation-confirmStartProcess.do');
	$('#' + this.formId).submit();
};

TaskOperation.prototype.completeTask = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/task-operation-completeTask.do');
	$('#' + this.formId).submit();
};

TaskOperation.prototype.rollbackPrevious = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/task-operation-rollbackPrevious.do');
	$('#' + this.formId).submit();
};

TaskOperation.prototype.rollbackStart = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/task-operation-rollbackStart.do');
	$('#' + this.formId).submit();
};

TaskOperation.prototype.rollbackInitiator = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/task-operation-rollbackInitiator.do');
	$('#' + this.formId).submit();
};

TaskOperation.prototype.transfer = function() {
	$('#modal form').attr('action', ROOT_URL + '/operation/task-operation-transfer.do');
	$('#modal').modal();
};

TaskOperation.prototype.delegateTask = function() {
	$('#modal form').attr('action', ROOT_URL + '/operation/task-operation-delegateTask.do');
	$('#modal').modal();
};

TaskOperation.prototype.delegateTaskCreate = function() {
	$('#modal form').attr('action', ROOT_URL + '/operation/task-operation-delegateTaskCreate.do');
	$('#modal').modal();
};

TaskOperation.prototype.communicate = function() {
	$('#modalCommunicate form').attr('action', ROOT_URL + '/operation/task-operation-communicate.do');
	$('#modalCommunicate').modal();
};

TaskOperation.prototype.approve = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/task-operation-completeTask.do');
	$('#_humantask_action_').val("同意");
	$('#' + this.formId).submit();
};

TaskOperation.prototype.reject = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/task-operation-completeTask.do');
	$('#_humantask_action_').val("反对");
	$('#' + this.formId).submit();
};

TaskOperation.prototype.abandon = function() {
	$('#' + this.formId).attr('action', ROOT_URL + '/operation/task-operation-completeTask.do');
	$('#_humantask_action_').val("弃权");
	$('#' + this.formId).submit();
};

TaskOperation.prototype.callback = function() {
	$('#modalCallback form').attr('action', ROOT_URL + '/operation/task-operation-callback.do');
	$('#modalCallback').modal();
};

TaskOperation.prototype.addCounterSign = function() {
	$('#modalCreateVote form').attr('action', ROOT_URL + '/operation/task-operation-createVote.do');
	$('#modalCreateVote').modal();
};

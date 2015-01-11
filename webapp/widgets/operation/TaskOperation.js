
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

TaskOperation.prototype.transfer = function() {
	$('#modal form').attr('action', ROOT_URL + '/operation/task-operation-transfer.do');
	$('#modal').modal();
};

TaskOperation.prototype.delegateTask = function() {
	$('#modal form').attr('action', ROOT_URL + '/operation/task-operation-delegateTask.do');
	$('#modal').modal();
};

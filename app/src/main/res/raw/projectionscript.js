 function print(txt) 
	{java.lang.System.out.println(txt);}  
	
compositeCollection=[];
varCollection=[];


// ==action obj	  
function Action(type,name,concept)
{
	this.name=name;
	this.type=type;this.concept=concept; 
}
Action.prototype.actionToDo=[];

Action.prototype.setOnConceptRecive=function(c,compActionName)
{
	Action.prototype.__defineSetter__('setaction', function(action)
									{
										Action.prototype.actionToDo.push(action);
										}); 
	var a={compName:compActionName,concept:c};
	this.setaction=a;
};

// ==actionSequance obj	 for CompositeAction in java class=====

function actionSequance(name) { 
	   this.name=name; 

		compositeCollection.push(this);
		}

actionSequance.prototype.actionList=[];

actionSequance.prototype.addAction=function(action){ 
		actionSequance.prototype.__defineSetter__('addaction', function(action)
				 { actionSequance.prototype.actionList.push(action);}); 
				  this.addaction=action;
		};

 actionSequance.prototype.printElements=function()
 { 
	for(var i=0;i< actionSequance.prototype.actionList.length;i++)
	{
		java.lang.System.out.println('the actionName is :  ' + actionSequance.prototype.actionList[i].name);
	}
};

// ==var conditionVar
//==========================
function conditionVar(type,name,concept)
{
	this.name=name;
	this.type=type;
	this.concept=concept;
	varCollection.push(this);
};

conditionVar.prototype.conditions=[];

conditionVar.prototype.setCond=function(condition)
{
	conditionVar.prototype.__defineSetter__('setcond', function(cond)
									{
										conditionVar.prototype.conditions.push(cond);
										});
	var re = /val(>|>=|<|<=|==)([\d])/
                var str = condition;
                var op = str.replace(re,"$1 , $2");


	//var newcond={operator:op,value:val};
	//this.setcond=newcond;
};


function monitoringPreProcesing(monitoringScript)
{

    var monitorRegex = /performMonitoringOn\s\((.*)\).*where.*(count|avg|sum)\(\)(>|>=|<|<=|==)([\d]).*forTime\s([\d]).*(days|hours|weeks);/
                    var str = monitoringScript;
                   var monitorstr= str.replace(monitorRegex,"$1,$2,$3,$4,$5,$6");


    return monitorstr.split(",");

}


//=================================================================


function setFrequency(freqamount,freqUnit,remainderAmount,remainderUnit)
	{
	   proj.setFrequency(freqUnit,freqamount);
	   proj.setReaminder(remainderUnit,remainderAmount);
	}

 function setStartTime(time)
 {
	 proj.setStartTime(time);
}

function onTriggerEvent(triggerAction)
{
proj.setTriggerAction(triggerAction);
}

function start(compositeName)
{
    proj.onStart(compositeName);

}
function defineVar(varName,condition)
{
    for(var i=0;i< varCollection.length;i++)
    	{
    	    if(varCollection[i].name==varName)

    	            varCollection[i].setCond(condition)
    	}
}

//===========================================================

//=== function  for iterate for all the actions in the compositeAction
// and insert them to projecction obj
function insertActionToProjection()
{ 
	for(var i=0;i< compositeCollection.length;i++)
	{
		var compositeName=compositeCollection[i].name;
		proj.addNewCompositeAction(compositeName,compositeCollection[i].order);
	    var actions=compositeCollection[i].actionList;
		for(var j=0;j< compositeCollection[i].actionList.length;j++)
		 {
		 print('adding the action : ' +actions[j].name);
			proj.addActionToComposite(compositeName,actions[j].type,actions[j].name,actions[j].concept);
			var ActionConceptsList=actions[j].actionToDo;
			for(var k=0;k< ActionConceptsList.length;k++)
			   {
				var conceptToReceive=ActionConceptsList[k].concept;
				 print('setting on recieve for the concept : ' +conceptToReceive);
				proj.setOnReceiveConcept(compositeName,conceptToReceive);
				}
		}
	}
};


function insertVarsToProjection()
{
    if( varCollection.length>0)
        proj.initMonitorAction();

	for(var i=0;i< varCollection.length;i++)
	{
		var condVar=varCollection[i];
		proj.defVar(condVar.name,condVar.concept,condVar.type);
	    var conditionsArr=condVar[i].conditions;
		for(var j=0;j< conditionsArr.length;j++)
		 {

			proj.addValueConstraint(condVar.name,conditionsArr[j].operator,conditionsArr[j].value);


		}
	}
};




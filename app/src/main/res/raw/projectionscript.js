 function print(txt) 
	{java.lang.System.out.println(txt);}  
	
compositeCollection=[];
varCollection=[];


// ==action obj	  
function Action(type,name,concept)
{
	this.name=name;
	this.type=type;this.concept=concept;
	this.actionToDo=[];
}


Action.prototype.setOnConceptRecive=function(c,compActionName)
{
	Action.prototype.__defineSetter__('setaction', function(action)
									{

										this.actionToDo.push(action);

										}); 
	var a={compName:compActionName,concept:c};
	this.setaction=a;
};

// ==actionSequance obj	 for CompositeAction in java class=====

function actionSequance(name) { 
	   this.name=name; 
        this.actionList=[];
		compositeCollection.push(this);
		}

//actionSequance.prototype.actionList=[];

actionSequance.prototype.addAction=function(action){ 
		actionSequance.prototype.__defineSetter__('addaction', function(action)
				 {this.actionList.push(action);});
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
function conditionVar(name,type,concept)
{
	this.name=name;
	this.type=type;
	this.concept=concept;
	varCollection.push(this);
	this.conditions=[];
	this.condOp='Or';
}
conditionVar.prototype.setCond=function(condition)
{

	conditionVar.prototype.__defineSetter__('setcondition', function(splittedCond)
									{
									        var splitted=splittedCond.split(",");

                                       var c={op:splitted[0],value:splitted[1]};

										this.conditions.push(c);
										});
			 if(condition.indexOf("&&") > -1)
			    this.condOp='And';

			var splittedCond=condition.split(/[\|\||&&]/);

             for(var i=0;i<splittedCond.length;i=i+2)
             {

                 var re = /val(>=|>|<=|<|==)([\d|\D])/
                 var str = splittedCond[i].replace(re,"$1,$2");
                 this.setcondition=str;
             }

};


function monitoringPreProcesing(monitoringScript)
{

    var monitorRegex = /performMonitoringOn\s\((.*)\).*where.*(count|avg|sum)\(\)(>|>=|<|<=|==)([\d]).*forTime\s([\d]).*(days|hours|weeks);/
                    var str = monitoringScript;

                   var monitorstr= monitoringScript.replace(monitorRegex,"proj.setTimeConstraint($5);proj.setAggregationConstraint(\'$2\',\'$3\',\'$4\');");
                   //eval(monitorstr);

    return monitorstr;

}

//=================================================================

function setReminder(remainderAmount,remainderUnit)
{
   if(proj.getType().name()=="Cyclic")
   {

    proj.setReaminder(remainderUnit,remainderAmount);
    }
}
function setFrequency(freqamount,freqUnit)
	{

       if(proj.getType().name()=="Cyclic")
       {
            proj.setFrequency(freqUnit,freqamount);
       }
	}

 function setStartTime(time)
 {
    if(proj.getType().name()=="Cyclic")
    {
        proj.setStartTime(time);
    }

}

function onTriggerEvent(triggerAction)
{
proj.setTriggerAction(triggerAction.name);
}

function start(compositeName)
{

    proj.onStart(compositeName.name);

}


//===========================================================

//=== function  for iterate for all the actions in the compositeAction
// and insert them to projecction obj
function insertActionToProjection()
{ 
	for(var i=0;i< compositeCollection.length;i++)
	{
		var compositeName=compositeCollection[i].name;
		 print('logging from JavaScript - (insertActionToProjection) :creating the compositeaction - '+compositeName);
		proj.addNewCompositeAction(compositeName,compositeCollection[i].order);
	    var actions=compositeCollection[i].actionList;
	     print('logging from JavaScript - (insertActionToProjection) : the composite : '+compositeName +' has '+actions.length +' actions');
		for(var j=0;j< actions.length;j++)
		 {
		 print('logging from JavaScript - (insertActionToProjection) :adding the('+j+') action : ' +actions[j].name +' to composite : '+compositeName);
			proj.addActionToComposite(compositeName,actions[j].type,actions[j].name,actions[j].concept);
			var ActionConceptsList=actions[j].actionToDo;
			 print('logging from JavaScript (insertActionToProjection) :the action : '+actions[j].name +'have '+ActionConceptsList.length+' onrecieveconcepts');
			for(var k=0;k< ActionConceptsList.length;k++)
			   {
				var conceptToReceive=ActionConceptsList[k].concept;
				var actionName=ActionConceptsList[k].compName.name;
				 print('logging from JavaScript (insertActionToProjection) =setting on recieve for the concept : ' +conceptToReceive);
				proj.setOnReceiveConcept(actionName,compositeName,conceptToReceive);
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
	    var conditionsArr=condVar.conditions;

		for(var j=0;j< conditionsArr.length;j++)
		 {

			proj.addValueConstraint(condVar.name,conditionsArr[j].op,conditionsArr[j].value);


		}
	}
};




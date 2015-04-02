 function print(txt) 
	{java.lang.System.out.println(txt);}  
	
compositeCollection=[];  
      
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

function setFrequency(freqamount,freqUnit,remainderAmount,remainderUnit)
	{
	   proj.setFrequency(freqUnit,freqamount);
	   proj.setReaminder(remainderUnit,remainderAmount);
	}

 function setStartTime(time)
 {
	 proj.setStartTime(time);
}
//===========================================================

//=== function  for iterate for all the actions in the compositeAction
// and insert them to projecction obj
function insertActionToProjection()
{ 
	for(var i=0;i< compositeCollection.length;i++)
	{
		var compositeName=compositeCollection[i].name;
	    var actions=compositeCollection[i].actionList;
		for(var j=0;j< compositeCollection[i].actionList.length;j++)
		 {
		 print('adding the action : ' +actions[j].name);
			proj.addActionToComposite(compositeName,actions[j].type,actions[j].name,actions[j].concept);
			var ActionConceptsList=actions[j].actionToDo;
			for(var k=0;k< ActionConceptsList.length;k++)
			   {
				var conceptToReceive=ActionConceptsList[k].concept;
				proj.setOnReceiveConcept(compositeName,conceptToReceive);
				}
		}
	}
};
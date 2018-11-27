TimeChooser {
	var <>noseCone;
	var <>lanes;
	var <>chosenLane;


init{
	lanes = List.new;
	      }

*new { ^ super.new.init}

addLane{
		arg aLane;
		this.lanes.add(aLane)}

//======= Printing  ==========
	printOn { | aStream |
		aStream << "a " << this.class.name << " with lanes " << this.lanes;
	    this.chosenLane.isNil.not.if(
			  {   aStream << " chosen lane " <<  this.chosenLane << " " ;

			});
		^aStream}


noseConeIsZero{   // still wonder if timechooser noseCones
		                    // should be simply  on or off,  active or inactive
		^this.noseCone == 0}

noseConeTooBig{
		^this.noseCone >1}


zeroWeightedLanes{
		^ lanes.select { arg eachTimeLane, i ;  eachTimeLane.hasZeroWeight }
	}

allLaneWeightsZero{
		^ (this.zeroWeightedLanes.size  == this.lanes.size)
		}

isActive{
		^ this.noseConeIsZero.not.and(  {this.allLaneWeightsZero.not } )
	        }

isNotActive{
		this.isActive.not}


chosenLaneIsFullyPlayable{
	this.chosenLane.isNil.if {"No timeLane chosen yet". postln ^false};
	^this.chosenLane.isFullyPlayable
	}


priorityBoarders {
			^ this.lanes.select({ arg eachLane, index; eachLane.hasInfiniteWeight})   }

hasPriorityBoarders {
			^ this.priorityBoarders.size>0}

hasTooManyPriorityBoarders{
		      ^ (this.priorityBoarders.size > noseCone)}

finiteNonZeroWeightedLanes{
		^ this.lanes.select({arg eachLane, index ; (eachLane.hasInfiniteWeight.not).and({eachLane.hasZeroWeight.not}) } )   }


chooseWinnerFromPriorityBoarders	 {
			 ^ this.priorityBoarders.choose;
			                         }

chooseWinnerFromFiniteNonZeroWeightedLanes{
		 var pool = List.new;
		 var poolWeights = List.new;
		  var normalizedWeights;
		  this.finiteNonZeroWeightedLanes.do(
			       { arg eachLane;
				     pool.add(eachLane);
			         poolWeights.add(eachLane.weight)}); // initialize pool weights
	      normalizedWeights = poolWeights.asArray.normalizeSum; //need to normalize
		  this.chosenLane_(pool.asArray.wchoose(normalizedWeights) )  ;// wchoose only works for arrays
		   (this.chosenLane == nil).if
		{"not enough non zero weighted time lanes available to meet nosecone demand".postln};
			^this.chosenLane
	        }

nonDeterministicLaneChoice {
	// 5 CASES
	//  zero noseCone
	this.noseConeIsZero.if {
			"nosecone in time chooser is zero- write code for this case".postln
			^Lane.empty; } ;//what to return? empty lane/ something else?

	// If nosecone bigger than 1 (incl inf case) change nose cone to 1 and say doing it.
	// NOT YET IMPLEMENTED
		this.noseConeTooBig.if {"nosecone in time chooser too big- write code for this case".postln};

	// If there are any priority boarders, one of those must win
	     this.hasPriorityBoarders.if({ ^ this.chooseWinnerFromPriorityBoarders;  });

	//  Nose cone must now  be  1  and there must be no priority boarders
		  ^ this.chooseWinnerFromFiniteNonZeroWeightedLanes;
     }



chooseLane{                                             // compare definition in Xhoosers
		this.nonDeterministicLaneChoice
	}



play {                            //   multiple hits of plain play always produce new choices
		                              // journal  of previous choices done in Xhooser
			this.chooseLane;                         // empties out previous choices
		    this.playChosenLane                  // may be interesting if we go nested

	}

duration{
		//  always duration based on most recent choice
		this.laneNotChosenYet.if {"No choice made yet - should not happen".postln; ^0};
	^	this.chosenLane.duration

}

laneNotChosenYet{
		^ this.choseLaneisNil.if { "chosen time Lane is nil".postln; ^ true};
	}


	}



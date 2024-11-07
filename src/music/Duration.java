package music;

import reaction.Mass;

public abstract class Duration extends Mass {
    // dont show up in screen, just decend dots and rest. therefore is abstract.
    public int nFlag = 0, nDot = 0; //number of flags
    public Duration(){
        super("NOTE");
    }

    //controller
    public void incFlag(){if(nFlag < 4){nFlag ++;}} //increment the nFlag
    public void decFlag(){if(nFlag > -2){nFlag --;}} //decrement the nFlag
    public void cycleDot(){nDot++; if(nDot > 3){nDot = 0;}} //cycle nDot

}

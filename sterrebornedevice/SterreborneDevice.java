package sterrebornedevice;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import pidevice.*;



class SterreborneDeviceRun implements SetCommandListener, GetCommandListener //, GpioPinListenerDigital 
{

    /* This class implements a RGPIO device on Raspberry PI.
     Sending and receiving of REPORT,EVENT,GET,SET.. is handled by PiDevice().
     This class implements :
     - GpioListener to create an event when a physical GPIO input pin changes state.
     - SetCommandListener (onSetCommand()) to set a physical GPIO pin when a SET command is received.
     - GetCommandListener (onGetCommand()) to return the value of a pin on a GET command.
     */


    DeviceOutput heating;   // digital output
    DeviceOutput boiler;    // digital output
//    DeviceInput button;     // digital input
    
    // GPIO pins corresponding to the digital input and digital output
    static GpioController gpio;

    static GpioPinDigitalOutput Gpio3;  // heating
    static GpioPinDigitalOutput Gpio4;  // boiler

    public void onSetCommand(DeviceOutput deviceOutput, String newValue) {
        // set GPIO pin corresponding to this deviceOutput to newValue
        
        boolean state=false;
        if (newValue.equals("High")) state=true;
        if (newValue.equals("Low")) state=false;
        
        if (deviceOutput == heating) {
            System.out.println("GPIO 3 (heating) set to " + newValue);
            Gpio3.setState(state);
        }
        if (deviceOutput == boiler) {
            System.out.println("GPIO 4 (boiler) set to " + newValue);
            Gpio4.setState(state);
        }
    }

    public String onGetCommand(DeviceInput deviceInput) {
        return ("");
    }

    /*
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        System.out.println("--> GPIO PIN <" + event.getPin().getName() + "> STATE CHANGE: " + event.getState());
//System.out.println("2? "+(event.getPin()==Gpio2));
//System.out.println("high? "+event.getState().isHigh());

        if (event.getState().isHigh()) {
            PiDevice.sendEvent("button", "High");
        } else {
            PiDevice.sendEvent("button", "Low");
        }
    }
    */

    public void start() {
        gpio = GpioFactory.getInstance();
//        Gpio2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
        Gpio3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "heating", PinState.LOW);
        Gpio4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "boiler", PinState.LOW);
//        Gpio2.addListener(this);

        // pass the model and the device pins to piDevice
        // From this information a Report string can be generated
        // PiDevice will call onSetCommand() and onGetCommand() when GET or SET is received
        PiDevice.deviceModel = "RASPBERRY";
//        button = PiDevice.addDigitalInput("button");
        heating = PiDevice.addDigitalOutput("heating");
        boiler = PiDevice.addDigitalOutput("boiler");

        PiDevice.printDevicePins();

        // 'this' can execute the  SET command for outputs
        heating.setCommandListener = this;  
        boiler.setCommandListener = this;

        // 'this' can execute the  GET command for inputs
//       button.getCommandListener = this;


        PiDevice.runDevice(2600, 2500);

// todo : convert listener thread back to in line so runDevice does not exit
        try {
            Thread.sleep(999999999);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

public class SterreborneDevice {

    public static void main(String[] args) {
        new SterreborneDeviceRun().start();
    }
}

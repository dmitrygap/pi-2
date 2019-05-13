import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

import com.pi4j.component.servo.ServoDriver;
import com.pi4j.component.servo.ServoProvider;
import com.pi4j.component.servo.impl.RPIServoBlasterProvider;
import com.pi4j.io.gpio.*;
import com.pi4j.platform.PlatformManager;
import com.pi4j.system.NetworkInfo;
import com.pi4j.system.SystemInfo;

public class PiTst {
    public static void main(String[] args) {
//        System.out.println("what is your name?");
//        String line = new Scanner(System.in).nextLine();
//        System.out.println("hello " + line + "!");

//        sysInfo(); // log detailed env info

        new PiTst().start();

    }

    private GpioController m_gpio;

    private void start() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override public void run() {
                System.out.println("ShutdownHook.");
                shutdown();
            }
        });

        System.out.println("PiTst started");
        try {
//            GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
            m_gpio = GpioFactory.getInstance(); // create gpio controller
            try {
//            test1(gpio);
//                test2(m_gpio);
                servo(m_gpio);
//                servoblaster(m_gpio);
            } finally {
                shutdown();
            }
        } catch (Throwable t) {
            System.out.println("Throwable: " + t);
            t.printStackTrace();
        }
        System.out.println("end");
    }

    private void servoblaster(GpioController gpio) throws IOException, InterruptedException {
        ServoProvider servoProvider = new RPIServoBlasterProvider();
        List<Pin> definedServoPins = servoProvider.getDefinedServoPins();
        Pin servoPin = definedServoPins.get(7);
        ServoDriver servo7 = servoProvider.getServoDriver(servoPin);
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 120000) { // 2 minutes
            for (int i = 50; i < 150; i++) {
                servo7.setServoPulseWidth(i); // Set raw value for this servo driver - 50 to 195
                Thread.sleep(10);
            }
            for (int i = 150; i > 50; i--) {
                servo7.setServoPulseWidth(i); // Set raw value for this servo driver - 50 to 195
                Thread.sleep(10);
            }
        }
        System.out.println("Exiting RPIServoBlasterExample");
    }

    private void servo(GpioController gpio) throws InterruptedException {
        // All Raspberry Pi models support a hardware PWM pin on GPIO_01.
        // Raspberry Pi models A+, B+, 2B, 3B also support hardware PWM pins: GPIO_23, GPIO_24, GPIO_26
        // by default we will use gpio pin #01
        Pin pin = RaspiPin.GPIO_26;
        GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(pin);

        // you can optionally use these wiringPi methods to further customize the PWM generator
        // see: http://wiringpi.com/reference/raspberry-pi-specifics/
        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        // sets the range register in the PWM generator. Default is 1024. You might want to set this to 180 is you wanted to express servo position in degrees.
        com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
        com.pi4j.wiringpi.Gpio.pwmSetClock(500);

        // set the PWM rate to 500
//        pwm.setPwm(10);

        pwm.setPwm(65);
        Thread.sleep(1000);
        pwm.setPwm(25);
        Thread.sleep(1000);

        pwm.setPwm(65);
        Thread.sleep(1000);
        pwm.setPwm(30);
        Thread.sleep(1000);

        pwm.setPwm(65);
        Thread.sleep(1000);
        pwm.setPwm(35);
        Thread.sleep(1000);

        for (int i = 25; i <= 110; i += 5) {
            System.out.println("Setting pwm to: " + i);
            pwm.setPwm(i);
            Thread.sleep(500);
        }

        pwm.setPwm(65);
        Thread.sleep(1000);
        pwm.setPwm(95);
        Thread.sleep(1000);

        pwm.setPwm(65);
        Thread.sleep(1000);
        pwm.setPwm(100);
        Thread.sleep(1000);

        pwm.setPwm(65);
        Thread.sleep(1000);
        pwm.setPwm(105);
        Thread.sleep(1000);



        pwm.setPwm(25);
        Thread.sleep(1000);
        pwm.setPwm(110);
        Thread.sleep(1000);

        pwm.setPwm(0);
    }

    private void shutdown() {
        if (m_gpio != null) {
            if (m_gpins != null) {
                System.out.println("set low to all pins");
                for (int i = 0; i < m_gpins.length; i++) {
                    GpioPinDigitalOutput gpin = m_gpins[i];
                    gpin.low();
                }
            }

            System.out.println("gpio.shutdown");
            // stop all GPIO activity/threads by shutting down the GPIO controller
            // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
            m_gpio.shutdown();
            m_gpio = null;
        }
    }

    GpioPinDigitalOutput[] m_gpins;

    private void test2(GpioController gpio) {
        Pin[] pins = new Pin[]{RaspiPin.GPIO_07, RaspiPin.GPIO_00, RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_01, RaspiPin.GPIO_04, RaspiPin.GPIO_05, RaspiPin.GPIO_06};
        m_gpins = new GpioPinDigitalOutput[pins.length];
        for (int i = 0; i < pins.length; i++) {
            Pin pin = pins[i];
            m_gpins[i] = gpio.provisionDigitalOutputPin(pin, "MyLED" + i, PinState.LOW);
        }

        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < m_gpins.length; i++) {
                GpioPinDigitalOutput gpin = m_gpins[i];
                gpin.pulse(300, true); // set second argument to 'true' use a blocking call
            }
        }


//        // START SNIPPET: usage-provision-input-pin-snippet
//        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
//        // (configure pin edge to both rising and falling to get notified for HIGH and LOW state
//        // changes)
//        GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02,             // PIN NUMBER
//                "MyButton",                   // PIN FRIENDLY NAME (optional)
//                PinPullResistance.PULL_DOWN); // PIN RESISTANCE (optional)
//        // START SNIPPET: usage-read-pin-snippet
//        // get explicit state enumeration for the GPIO pin associated with the button
//        PinState myButtonState = myButton.getState();
//
//        // use convenience wrapper method to interrogate the button state
//        boolean buttonPressed = myButton.isHigh();
//        // END SNIPPET: usage-read-pin-snippet
//
//        // START SNIPPET: usage-register-listener-snippet
//        // create and register gpio pin listener
//        myButton.addListener(new GpioUsageExampleListener());
//        // END SNIPPET: usage-register-listener-snippet
//
//        // START SNIPPET: usage-trigger-snippet
//        // create a gpio synchronization trigger on the input pin
//        // when the input state changes, also set LED controlling gpio pin to same state
//        myButton.addTrigger(new GpioSyncStateTrigger(myLed));
//        // END SNIPPET: usage-trigger-snippet
//
//        public static class GpioUsageExampleListener implements GpioPinListenerDigital {
//            @Override
//            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
//                // display pin state on console
//                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = "
//                        + event.getState());
//            }
//        }





//        GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(RaspiPin.GPIO_24);
//        pwm.setPwm(500);


// RGB LED on Raspberry Pi using Java
// https://www.admfactory.com/rgb-led-on-raspberry-pi-using-java/
//        Gpio.wiringPiSetup()
//        //Initialize the wiringPi library, this is needed for PWM.
//        SoftPwm.softPwmCreate(int pin, int value, int range)
//        //This creates a software-controlled PWM pin. You can use any GPIO pin and the pin numbering will be that of the wiringPiSetup function you used. Use 100 for the pwmRange, then the value can be anything from 0 (off) to 100 (fully on) for the given pin.
//        SoftPwm.softPwmWrite(int pin, int value)
//        //This updates the PWM value on the given pin. The value is checked to be in-range and pins that haven’t previously been initialized via softPwmCreate will be silently ignored.

    }

    public static void sysInfo() {

        try {
            // display a few of the available system information properties
            System.out.println("----------------------------------------------------");
            System.out.println("PLATFORM INFO");
            System.out.println("----------------------------------------------------");
            try{System.out.println("Platform Name     :  " + PlatformManager.getPlatform().getLabel());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Platform ID       :  " + PlatformManager.getPlatform().getId());}
            catch(UnsupportedOperationException ex){}
            System.out.println("----------------------------------------------------");
            System.out.println("HARDWARE INFO");
            System.out.println("----------------------------------------------------");
            try{System.out.println("Serial Number     :  " + SystemInfo.getSerial());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("CPU Revision      :  " + SystemInfo.getCpuRevision());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("CPU Architecture  :  " + SystemInfo.getCpuArchitecture());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("CPU Part          :  " + SystemInfo.getCpuPart());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("CPU Temperature   :  " + SystemInfo.getCpuTemperature());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("CPU Core Voltage  :  " + SystemInfo.getCpuVoltage());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("CPU Model Name    :  " + SystemInfo.getModelName());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Processor         :  " + SystemInfo.getProcessor());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Hardware          :  " + SystemInfo.getHardware());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Hardware Revision :  " + SystemInfo.getRevision());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Is Hard Float ABI :  " + SystemInfo.isHardFloatAbi());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Board Type        :  " + SystemInfo.getBoardType().name());}
            catch(UnsupportedOperationException ex){}

            System.out.println("----------------------------------------------------");
            System.out.println("MEMORY INFO");
            System.out.println("----------------------------------------------------");
            try{System.out.println("Total Memory      :  " + SystemInfo.getMemoryTotal());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Used Memory       :  " + SystemInfo.getMemoryUsed());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Free Memory       :  " + SystemInfo.getMemoryFree());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Shared Memory     :  " + SystemInfo.getMemoryShared());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Memory Buffers    :  " + SystemInfo.getMemoryBuffers());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Cached Memory     :  " + SystemInfo.getMemoryCached());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("SDRAM_C Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_C());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("SDRAM_I Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_I());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("SDRAM_P Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_P());}
            catch(UnsupportedOperationException ex){}

            System.out.println("----------------------------------------------------");
            System.out.println("OPERATING SYSTEM INFO");
            System.out.println("----------------------------------------------------");
            try{System.out.println("OS Name           :  " + SystemInfo.getOsName());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("OS Version        :  " + SystemInfo.getOsVersion());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("OS Architecture   :  " + SystemInfo.getOsArch());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("OS Firmware Build :  " + SystemInfo.getOsFirmwareBuild());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("OS Firmware Date  :  " + SystemInfo.getOsFirmwareDate());}
            catch(UnsupportedOperationException | ParseException ex){}

            System.out.println("----------------------------------------------------");
            System.out.println("JAVA ENVIRONMENT INFO");
            System.out.println("----------------------------------------------------");
            System.out.println("Java Vendor       :  " + SystemInfo.getJavaVendor());
            System.out.println("Java Vendor URL   :  " + SystemInfo.getJavaVendorUrl());
            System.out.println("Java Version      :  " + SystemInfo.getJavaVersion());
            System.out.println("Java VM           :  " + SystemInfo.getJavaVirtualMachine());
            System.out.println("Java Runtime      :  " + SystemInfo.getJavaRuntime());

            System.out.println("----------------------------------------------------");
            System.out.println("NETWORK INFO");
            System.out.println("----------------------------------------------------");

            // display some of the network information
            System.out.println("Hostname          :  " + NetworkInfo.getHostname());
            for (String ipAddress : NetworkInfo.getIPAddresses())
                System.out.println("IP Addresses      :  " + ipAddress);
            for (String fqdn : NetworkInfo.getFQDNs())
                System.out.println("FQDN              :  " + fqdn);
            for (String nameserver : NetworkInfo.getNameservers())
                System.out.println("Nameserver        :  " + nameserver);

            System.out.println("----------------------------------------------------");
            System.out.println("CODEC INFO");
            System.out.println("----------------------------------------------------");
            try{System.out.println("H264 Codec Enabled:  " + SystemInfo.getCodecH264Enabled());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("MPG2 Codec Enabled:  " + SystemInfo.getCodecMPG2Enabled());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("WVC1 Codec Enabled:  " + SystemInfo.getCodecWVC1Enabled());}
            catch(UnsupportedOperationException ex){}

            System.out.println("----------------------------------------------------");
            System.out.println("CLOCK INFO");
            System.out.println("----------------------------------------------------");
            try{System.out.println("ARM Frequency     :  " + SystemInfo.getClockFrequencyArm());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("CORE Frequency    :  " + SystemInfo.getClockFrequencyCore());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("H264 Frequency    :  " + SystemInfo.getClockFrequencyH264());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("ISP Frequency     :  " + SystemInfo.getClockFrequencyISP());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("V3D Frequency     :  " + SystemInfo.getClockFrequencyV3D());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("UART Frequency    :  " + SystemInfo.getClockFrequencyUART());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("PWM Frequency     :  " + SystemInfo.getClockFrequencyPWM());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("EMMC Frequency    :  " + SystemInfo.getClockFrequencyEMMC());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("Pixel Frequency   :  " + SystemInfo.getClockFrequencyPixel());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("VEC Frequency     :  " + SystemInfo.getClockFrequencyVEC());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("HDMI Frequency    :  " + SystemInfo.getClockFrequencyHDMI());}
            catch(UnsupportedOperationException ex){}
            try{System.out.println("DPI Frequency     :  " + SystemInfo.getClockFrequencyDPI());}
            catch(UnsupportedOperationException ex){}

            System.out.println();
            System.out.println();
            System.out.println("Exiting SystemInfoExample");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void test1(GpioController gpio) throws InterruptedException {
//             provision broadcom gpio pin #02 as an output pin and turn on
//            final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_16, "MyLED", PinState.HIGH);


        // provision gpio pin #01 as an output pin and turn on
        final GpioPinDigitalOutput pin0 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLED", PinState.HIGH);
        final GpioPinDigitalOutput pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "MyLED", PinState.HIGH);
        final GpioPinDigitalOutput pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "MyLED", PinState.HIGH);
        final GpioPinDigitalOutput pin3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "MyLED", PinState.HIGH);
        System.out.println("--> GPIO state should be: ON");

        // set shutdown state for this pin
        pin0.setShutdownOptions(true, PinState.LOW);
        pin1.setShutdownOptions(true, PinState.LOW);
        pin2.setShutdownOptions(true, PinState.LOW);
        pin3.setShutdownOptions(true, PinState.LOW);

        Thread.sleep(3000);

        // turn off gpio pin #01
        pin0.low();
        pin1.low();
        pin2.low();
        pin3.low();
        System.out.println("--> GPIO state should be: OFF");

        Thread.sleep(3000);

        // toggle the current state of gpio pin #01 (should turn on)
        pin0.toggle();
        pin1.toggle();
        pin2.toggle();
        pin3.toggle();
        System.out.println("--> GPIO state should be: ON");

        Thread.sleep(3000);

        // toggle the current state of gpio pin #01  (should turn off)
        pin0.toggle();
        pin1.toggle();
        pin2.toggle();
        pin3.toggle();
        System.out.println("--> GPIO state should be: OFF");

        Thread.sleep(3000);

        // turn on gpio pin #01 for 1 second and then off
        System.out.println("--> GPIO state should be: ON for only 1 second");
        pin0.pulse(1000, true); // set second argument to 'true' use a blocking call
        pin1.pulse(1000, true); // set second argument to 'true' use a blocking call
        pin2.pulse(1000, true); // set second argument to 'true' use a blocking call
        pin3.pulse(1000, true); // set second argument to 'true' use a blocking call

        pin0.low();
        pin1.low();
        pin2.low();
        pin3.low();
    }
}

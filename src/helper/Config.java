package src.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
 
public class Config {
	
    private static final String FILE_CONFIG = ""
    		+ "resources/config.properties";
    private static Config instance = null;
    private Properties properties = new Properties();
 
    /**
     * Use singleton pattern to create ReadConfig object one time and use
     * anywhere
     *
     * @return instance of ReadConfig object
     */
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
            instance.readConfig();
        }
        return instance;
    }
 
    // Get property
    public int getAsInteger(String key) {
		String trimmer = this.getTrimmed(key);
		return Integer.parseInt(trimmer);
	}

	public long getAsLong(String key) {
		String trimmer = this.getTrimmed(key);
		return Long.parseLong(trimmer);
	}

	public double getAsDouble(String key) {
		String trimmer = this.getTrimmed(key);
		return Double.parseDouble(trimmer);
	}

	public float getAsFloat(String key) {
		String trimmer = this.getTrimmed(key);
		return Float.parseFloat(trimmer);
	}

	public boolean getAsBoolean(String key) {
		String valueString = this.getTrimmed(key);
		valueString = valueString.toLowerCase();
		if ("true".equals(valueString)) {
			return true;
		} else {
			return false;
		}
	}

	public String getAsString(String key) {
		return this.getTrimmed(key);
	}

	private String getTrimmed(String name) {
		String value = this.properties.getProperty(name);
		return value.trim();
	}
 
    // Read file properties
    private void readConfig() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(FILE_CONFIG);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close objects
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Config
    // GNB config
	public static int gnbProcessPerSecond = getInstance().getAsInteger("gnb_process_per_second");
	public static double gnbCarMeanTranfer = getInstance().getAsDouble("gnb_car_mean_tranfer");
	
	// RSU config
	public static int rsuNumbers = getInstance().getAsInteger("rsu_numbers");
	public static String xList = getInstance().getAsString("list_rsu_xcoord");
	public static String yList = getInstance().getAsString("list_rsu_ycoord");
	public static String zList = getInstance().getAsString("list_rsu_zcoord");
	public static float rsuCoverRadius = getInstance().getAsFloat("rsu_cover_radius");
	public static int rsuProcessPerSecond = getInstance().getAsInteger("rsu_process_per_second");
	public static double rsuCarMeanTranfer = getInstance().getAsDouble("rsu_car_mean_tranfer");
	public static double rsuRsuMeanTranfer = getInstance().getAsDouble("rsu_rsu_mean_tranfer");
	public static double rsuGnbMeanTranfer = getInstance().getAsDouble("rsu_gnb_mean_tranfer");
	public static int nStatesRsu = getInstance().getAsInteger("number_state_rsu");
	public static int nActionsRsu = getInstance().getAsInteger("number_action_rsu");

	// Car config
	public static int carSpeed = getInstance().getAsInteger("car_speed");
	public static String carAppearStrategy = getInstance().getAsString("car_appear_strategy");
	public static String carPackageStrategy = getInstance().getAsString("car_package_strategy");
	public static float carCoverRadius = getInstance().getAsFloat("car_cover_radius");
	public static int carProcessPerSecond = getInstance().getAsInteger("car_process_per_second");
	public static double carCarMeanTranfer = getInstance().getAsDouble("car_car_mean_tranfer");
	public static double carRsuMeanTranfer = getInstance().getAsDouble("car_rsu_mean_tranfer");
	public static double carGnbMeanTranfer = getInstance().getAsDouble("car_gnb_mean_tranfer");
    public static int nStatesCar = getInstance().getAsInteger("number_state_car");
	public static int nActionsCar = getInstance().getAsInteger("number_action_car");

	// Message
	public static int maxSize = getInstance().getAsInteger("max_size");
	public static int maxCpuCycle = getInstance().getAsInteger("max_cpu_cycle");

	public static String messageSizeString = getInstance().getAsString("message_size");
	static String[] messageSizeStrings = messageSizeString.split("_");
	static String sizeString = messageSizeStrings[messageSizeStrings.length-1];
	public static double messageSize = Double.parseDouble(sizeString);

	public static String messageCpuString = getInstance().getAsString("message_size");
	static String[] messageCpuStrings = messageCpuString.split("_");
	static String cpuString = messageCpuStrings[messageCpuStrings.length-1];
	public static double messageCpuCycle = Double.parseDouble(cpuString);

	// Policy
	public static double epsilon = getInstance().getAsDouble("epsilon");
	public static double w = getInstance().getAsDouble("w");
	public static double minEpsilon = getInstance().getAsDouble("min_epsilon");
	public static double decayRateEpsilon = getInstance().getAsDouble("decay_rate_epsilon");

	// DQN
	public static double learningRate = getInstance().getAsDouble("learning_rate");
	public static double discountingFactor = getInstance().getAsDouble("discounting_factor");
	public static int batchSize = getInstance().getAsInteger("batch_size");
	public static int memoryCapacity = getInstance().getAsInteger("memory_capacity");
	public static int timeUpdateOnlineModel = getInstance().getAsInteger("time_update_online_model");
	public static int timeUpdateTargetModel = getInstance().getAsInteger("time_update_target_model");
	public static int hiddenLayer = getInstance().getAsInteger("hidden_layer");
	public static double minDelta = getInstance().getAsDouble("min_delta");

	// MAB + DQN
	public static double minProbChooseMab = getInstance().getAsDouble("min_prob_choose_mab");
	public static double decayRateChooseMab = getInstance().getAsDouble("decay_rate_prob_choose_mab");
	public static int thresholdStable = getInstance().getAsInteger("threadhold_stable");

	// Other
	public static double defaultDropTime = getInstance().getAsDouble("default_drop_time");
	public static double decayRateMean = getInstance().getAsDouble("decay_rate_mean");
	public static double pL = getInstance().getAsDouble("default_pl");
	public static double pR = getInstance().getAsDouble("default_pr");
	public static double simTime = getInstance().getAsDouble("simTime");
	public static double cycleTime = getInstance().getAsDouble("cycle_time");
	public static double roadLength = getInstance().getAsDouble("road_length");
	
	public static String dumpDelayDetail = getInstance().getAsString("dump_delay_detail");
	public static String dumpDelayGeneral = getInstance().getAsString("dump_delay_general");
	public static String messageDetail = getInstance().getAsString("message_detail");
	public static String loggingFile = getInstance().getAsString("logging_file");
	public static String resultFolder = getInstance().getAsString("result_folder");
	public static String expName = getInstance().getAsString("exp_name");
	public static String optimizer = getInstance().getAsString("optimizer");
	
	static String[] carPackageStrategyStrings = carPackageStrategy.split("_");
	static String numMessagePerSecondString = carPackageStrategyStrings[carPackageStrategyStrings.length-1];
	public static double numMessagePerSecond = Double.parseDouble(numMessagePerSecondString);

	static String[] carAppearStrategyStrings = carAppearStrategy.split("_");
	static String timeACarAppearString = carAppearStrategyStrings[carAppearStrategyStrings.length-1];
	public static double timeACarAppear = Double.parseDouble(timeACarAppearString);

	public static String dumpDelayGeneralPath = resultFolder + "/" + dumpDelayGeneral;
	public static String dumpDetailFolder = resultFolder + "/" + expName;
	public static String dumpDelayDetailPath = dumpDetailFolder + "/" + dumpDelayDetail;
	public static String MessageDetailPath = dumpDetailFolder + "/" + messageDetail;
	public static String loggingFilePath = dumpDetailFolder + "/" + loggingFile;

}
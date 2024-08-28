import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class PRNProcessor {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <path to json file>");
            return;
        }

        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];
        
        try {
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONObject json = new JSONObject(content);
            String destinationValue = findDestinationValue(json);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            String randomString = randomAlphaNumeric(8);
            String hashInput = prnNumber + destinationValue + randomString;
            String hash = generateMD5(hashInput);
            System.out.println(hash + ";" + randomString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JSONObject json) {
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String randomAlphaNumeric(int count) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (random.nextFloat() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private static String generateMD5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

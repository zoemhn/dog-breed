package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        String url = "https://dog.ceo/api/breed/" + breed + "/list";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String data = response.body().string();
            JSONObject jsonob = new JSONObject(data);

            String status  = jsonob.getString("status");
            if (!status.equals("success")) {
                throw new BreedNotFoundException("Breed not found");
            }

            JSONArray breeds = jsonob.getJSONArray("message");
            List<String> subbreeds = new ArrayList<>();
            for (int i = 0; i < breeds.length(); i++) {
                subbreeds.add(breeds.getString(i));
            }
            return subbreeds;
        }
        catch (IOException e) {
            throw new BreedNotFoundException("API call failed");
        }
    }
}
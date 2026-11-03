package vineeth.learnings;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import static vineeth.learnings.constants.TemperatureConstants.*;
import vineeth.learnings.constants.OpenAIModelList;

/**
 * Temperature refers the randomness and creativity involved in the model's output.
 * It ranges from 0.0 (no random - deterministic) to 1.0 (most random)
 * 
 * @author Vineeth
 *
 */
public class TemperatureImplementation {

	static OpenAIClient client = OpenAIOkHttpClient.fromEnv();
	
	public static void main(String[] args) {
		
		String question1 = "What is externalized configuration";

		System.out.println("--------Querying the same question with MIN temperature--------\n\n");
		System.out.println("Question : "+question1+"\n");
		for(int i=0;i<3;i++) {
			callLLMWithTemperature(question1, MIN_TEMPERATURE);	//static import
		}
		
		System.out.println("\n\n--------Querying the same question with MAX temperature--------\n\n");
		System.out.println("Question : "+question1+"\n");
		for(int i=0;i<3;i++) {
			callLLMWithTemperature(question1, MAX_TEMPERATURE); //static import
		}
	}

	private static void callLLMWithTemperature(String question1,double temperature) {
		
		String systemMessage = "You're Tech lead with 10YOE. Explain the questions in 50 words, Respond with repeated if the question is getting repeated";
		
		
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
													.model(OpenAIModelList.MiniModel)
													.addSystemMessage(systemMessage)
													.addUserMessage(question1)
													.temperature(temperature)
													.build();
		
		String response = connectWithLLM(params);
		System.out.println(response);
	}
	
	private static String connectWithLLM(ChatCompletionCreateParams params) {
		
		return client.chat()
					.completions()
					.create(params)
					.choices()
					.get(0)
					.message()
					.content()
					.orElse("");
	}
}

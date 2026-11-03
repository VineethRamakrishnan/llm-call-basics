package vineeth.learnings;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.completions.CompletionUsage;

import vineeth.learnings.constants.OpenAIModelList;

/**
 * This class demonstrates the control over the token
 * 		a. Limiting the output thru maxtoken
 * 		b. Getting the token usage - Input/output/Total
 * 
 * @author Vineeth
 *
 */
public class TokenControlImplementation {

	static OpenAIClient client = OpenAIOkHttpClient.fromEnv();
	
	public static void main(String[] args) {
		
		//demonstrateMaxTokenUsage();
		explainTokenUsage();
	}

	/**
	 * Explaining about maxToken API - It limits the output even it exceeds
	 */
	private static void demonstrateMaxTokenUsage() {
		
		String systemMessage = "You're Tech lead with 10YOE. Explain the questions in 50 words, Respond with repeated if the question is getting repeated";
		
		String query1 = "Explain me about lifecycle hooks in Angular JS";
		
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
												.model(OpenAIModelList.MiniModel)
												.addSystemMessage(systemMessage)
												.addUserMessage(query1)
												.maxCompletionTokens(20) 		//This takes precedence over system message configuration and abtruptly cuts the output
												.build();
		
		String response = connectWithLLM(params);
		
		System.out.println("\n\n----------Limiting the output through maxCompletionTokens--------\n\n");
		System.out.println("Query : "+query1);
		System.out.println(response);
	}
	
	/**
	 * Explaining about the token usage
	 */
	private static void explainTokenUsage() {
		
		String systemMessage = "You're Tech lead with 10YOE. Explain the questions in 50 words, Respond with repeated if the question is getting repeated";
		
		String query1 = "Explain me about Virtual DOM in Angular JS";
		
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
												.model(OpenAIModelList.MiniModel)
												.addSystemMessage(systemMessage)
												.addUserMessage(query1)
												.build();
		
		ChatCompletion chatCompletionResponse = client.chat().completions().create(params);
		String response = chatCompletionResponse.choices().get(0).message().content().orElse("");
		CompletionUsage usage = chatCompletionResponse.usage().orElseThrow();
		
		System.out.println("\n\n----------Token usage implementation--------\n\n");
		System.out.println("Query : "+query1);
		System.out.println(response);

		System.out.println("\n ----- Token used ----- \n");
		System.out.println("Input tokens used : "+usage.promptTokens());
		System.out.println("Output tokens used : "+usage.completionTokens());
		System.out.println("Total tokens used : "+usage.totalTokens());
	
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

package vineeth.learnings;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import vineeth.learnings.constants.OpenAIModelList;

public class ChatHistoryImplementation {

	static OpenAIClient client = OpenAIOkHttpClient.fromEnv();
	
	public static void main(String[] args) {
	
		chatWithCompletionCreateParamsWithSDK();
		
	}

	/**
	 * Method to demonstrate to instill the attribute to make LLM remember the conversation
	 */
	private static void chatWithCompletionCreateParamsWithSDK() {
		
		String systemMessage = "You're Tech lead with 10YOE. Explain the questions in 50 words, Respond with repeated if the question is getting repeated";
		String question1 = "What is externalized configuration";
		String question2 = "Why do we need blue green deployment";
		
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
													.model(OpenAIModelList.LunaModel)
													.addSystemMessage(systemMessage)
													.addUserMessage(question1)
													.build();
		
		String response = connectWithLLM(params);
		System.out.println("Question 1: "+question1);
		System.out.println("AI Response : "+response);
		
		ChatCompletionCreateParams seocndConversationParams = ChatCompletionCreateParams.builder()
				.model(OpenAIModelList.LunaModel)
				.addSystemMessage(systemMessage)
				.addAssistantMessage(response)		//It helps to remember the history of older conversation as well
				.addUserMessage(question2)
				.build();
		
		String secondResponse = connectWithLLM(seocndConversationParams);
		System.out.println("\n\nQuestion 2: "+question2);
		System.out.println("AI Response : "+secondResponse);
		
		/**
		 * Scenario : We're asking the first question again in the third query
		 * 
		 * TODO: Expected : It should returns "repeated" as added in the system message
		 */
		ChatCompletionCreateParams thirdConversationParams = ChatCompletionCreateParams.builder()
				.model(OpenAIModelList.LunaModel)
				.addSystemMessage(systemMessage)
				.addAssistantMessage(response)				//We need to add each response to LLM params so that would be remembered
				.addAssistantMessage(secondResponse)		//It helps to remember the history of older conversation as well
				.addUserMessage(question2)
				.build();
		
		String thirdResponse = connectWithLLM(thirdConversationParams);
		System.out.println("\n\nQuestion 3: "+question2);
		System.out.println("AI Response : "+thirdResponse);
		
	}
	
	/**
	 * Helper method to chat and extract the response from the LLM
	 * 
	 * @param params
	 * @return
	 */
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

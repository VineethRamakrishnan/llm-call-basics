package vineeth.learnings;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

import vineeth.learnings.constants.OpenAIModelList;

/**
 * This class demonstrates how the AI replies can be streamed as we see in the normal AI apps
 * @author Vineeth
 *
 */
public class BasicStreamingJavaImplementation {

	//Creating the client based on the configuration details in the properties file 
	static OpenAIClient client = OpenAIOkHttpClient.fromEnv();
	
	public static void main(String[] args){
	
		String query = "Explain Azure Devops in layman terms with 5 sentences";
		String systemMessage = " You're Devops expert, answer the queries accordingly..";
		
		generateStreamingResponse(systemMessage,query);
		generateNormalResponse(systemMessage,query);

	}
	
	/**
	 * Basic streaming explanation
	 * @param query
	 */
	static void generateStreamingResponse(String systemMessage, String query){
		
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
															.model(OpenAIModelList.LunaModel)
															.addSystemMessage(systemMessage)
															.addUserMessage(query)
															.build();
		
		System.out.println("Printing as and when it arrives...\n");
		
		//try with resources
		try(StreamResponse<ChatCompletionChunk> streamResponse = client.chat().completions().createStreaming(params)){
			
			streamResponse.stream()
				.flatMap(chunk -> chunk.choices().stream())					//Getting choices from each stream
				.flatMap(choice -> choice.delta().content().stream())		// Getting content from each chunk
				.forEach(System.out::print);
		}
				
		System.out.println("\n=======================================");
	}
	
	
	/**
	 * Normal response method for reference
	 * @param query
	 */
	static void generateNormalResponse(String systemMessage, String query) {

			//Creating request params
			ResponseCreateParams params = ResponseCreateParams
			        							.builder()
			        							.instructions(systemMessage)
			        							.input(query)
			        							.model(OpenAIModelList.LunaModel)
			        							.build();
				
			Response response = client.responses().create(params);
				
			System.out.println("\nPrinting all at once..\n");
			//flattening the nested output
			response.output().stream()
		       .flatMap(item -> item.message().stream())
		       .flatMap(message -> message.content().stream())
		       .flatMap(content -> content.outputText().stream())
		       .forEach(outputText -> System.out.println(outputText.text()));
	}

}

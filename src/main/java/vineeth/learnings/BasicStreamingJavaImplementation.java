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
		
		generateStreamingResponse();
		generateNormalResponse();

	}
	
	static void generateStreamingResponse(){
		
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
															.model(OpenAIModelList.LunaModel)
															.addSystemMessage("You're devops expert and answer accoringly")
															.addUserMessage("Explain Azure Devops in layman terms with 5 sentences")
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
	
	static void generateNormalResponse() {

			//Creating request params
			ResponseCreateParams params = ResponseCreateParams
			        							.builder()
			        							.input("Explain Azure Devops in layman terms with 5 sentences")
			        							.model(OpenAIModelList.LunaModel)
			        							.build();
				
			Response response = client.responses().create(params);
				
			System.out.println("\n Printing all at once..\n");
			//flattening the nested output
			response.output().stream()
		       .flatMap(item -> item.message().stream())
		       .flatMap(message -> message.content().stream())
		       .flatMap(content -> content.outputText().stream())
		       .forEach(outputText -> System.out.println(outputText.text()));
	}

}

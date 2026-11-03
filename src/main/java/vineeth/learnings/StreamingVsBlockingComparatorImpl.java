package vineeth.learnings;

import static vineeth.learnings.constants.OpenAIModelList.*;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionChunk;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

/**
 * This class demonstrates how the streaming and blocking implementations differ and 
 * it explains in advanced way than BasicStreamingJavaImplementation class
 * 
 * @author Vineeth
 *
 */
public class StreamingVsBlockingComparatorImpl {

	//Client configuration
	static OpenAIClient client = OpenAIOkHttpClient.fromEnv();
	
	static long startTime;
	static long blockingTime;
	static long streamingTime;
	static long firstTextAtStreaming;
	
	public static void main(String[] args) {
		
		String systemMessage = "";
		String userMessage = "Explain the Java Collections Framework in 15 short points";
		
		//creating the chat params
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
				.model(MiniModel)
				.addUserMessage(userMessage)
				.build();
		
		System.out.println("\n===========STREAMING===============\n");
		executeInStreamingImplementation(params,systemMessage,userMessage);
		
		System.out.println("\n===========BLOCKING===============\n");
		executeInBlockingImplementation(params,systemMessage,userMessage);
		
		
		
		System.out.println("\n\nBlocking Time : "+blockingTime);
		System.out.println("Streaming Time : "+streamingTime);
		System.out.println("First Text seen at Streaming : "+firstTextAtStreaming);

	}

	/**
	 * Blocking implementation - It shows when the first token is displayed to the user, total time taken, etc.
	 * @param systemMessage
	 * @param userMessage
	 */
	static void executeInBlockingImplementation(ChatCompletionCreateParams params, String systemMessage,String userMessage) {
		
		startTime = System.currentTimeMillis();
		
		ChatCompletion completion = client.chat().completions().create(params);
		
		blockingTime = System.currentTimeMillis() - startTime;
		
		String answer = completion.choices().get(0).message().content().orElse("");
		
		System.out.println(answer);
		
	}
	
	
	/**
	 * Streaming implementation - It shows when the first token is displayed to the user, total time taken, etc.
	 * @param systemMessage
	 * @param userMessage
	 */
	static void executeInStreamingImplementation(ChatCompletionCreateParams params, String systemMessage, String userMessage) {
		
		startTime = System.currentTimeMillis();
		
		//try with resources
		try(StreamResponse<ChatCompletionChunk> completionchunks = client.chat().completions().createStreaming(params)){
			
			completionchunks.stream()
				.flatMap(chunk -> chunk.choices().stream())
				.flatMap(choice -> choice.delta().content().stream())
				.forEach(text -> {
					if(firstTextAtStreaming == 0) {
						firstTextAtStreaming = System.currentTimeMillis() - startTime;
					}
					
					System.out.print(text);
				});
		}
		
		streamingTime = System.currentTimeMillis() - startTime;
	}
	
}

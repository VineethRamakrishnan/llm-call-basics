package vineeth.learnings;

import static vineeth.learnings.constants.StringConstants.OPENAI_API_KEY;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialResponse;
import dev.langchain4j.model.chat.response.PartialResponseContext;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.TokenUsage;
import vineeth.learnings.constants.OpenAIModelList;

/**
 * This class is for demonstrating the implementation of the streaming concepts in langchain4j framework
 * @author Vineeth
 *
 */
public class StreamingLanChain4JImplementation {

	static OpenAiStreamingChatModel streaming = OpenAiStreamingChatModel.builder()
			.apiKey(System.getenv(OPENAI_API_KEY))
			.modelName(OpenAIModelList.MiniModel)
			.build();
	
	static String question = "Explain kubernets in simple terms within 600 words";
	static long start;
	
	public static void main(String[] args) {
		
		//streamResponseImplementation();
		
		//demonstrating the retry mechanism - manually stopping after 3 seconds and retrying from where it has been left
		streamResponseImplementationWithRetries();

	}
	
	static void streamResponseImplementation() {
		
		CompletableFuture<ChatResponse> streamResponse = new CompletableFuture<>();
				
		//full implementation of streaming implementation in LC4J
		streaming.chat(
				
				"Explain kubernets in simple terms within 200 words",	//question
				
				new StreamingChatResponseHandler() {
					
					int counter=0;
					
					
					//It represents each chunk getting received from the LLM
					@Override
					public void onPartialResponse(PartialResponse partialResponse, PartialResponseContext context) {
						
						System.out.print(partialResponse.text());
						counter++;
						
					}
					
					@Override
					public void onError(Throwable error) {
						
						System.out.println("Inside onError method, Some error occurred please check after some time");
						streamResponse.completeExceptionally(error);
						
					}
					
					@Override
					public void onCompleteResponse(ChatResponse completeResponse) {
						
						System.out.println("\nInside onCompleteResponse method, successfully completed!!");
						System.out.println("Total tokens : "+counter);
						
						streamResponse.complete(completeResponse);
					}
				}
		);
		
		ChatResponse response = streamResponse.join();
		TokenUsage usage = response.tokenUsage();
		
		System.out.println("\n");
	    System.out.println("Full answer length : " + response.aiMessage().text().length() + " characters");
	    System.out.println("Finish reason      : " + response.finishReason());
	    System.out.println("Input tokens       : " + usage.inputTokenCount());
	    System.out.println("Output tokens      : " + usage.outputTokenCount());
	}
	
	
	/**
	 * Steps :
	 * 
	 * 		1. chat with LLM and in streaming mode
	 * 		2. Manually stop the streaming after 3 seconds
	 * 		3. Again chat with LLM and generating response from where it has been left
	 */
	private static void streamResponseImplementationWithRetries() {
	
		StringBuilder response = new StringBuilder();	
		CompletableFuture<Boolean> streamResponse = new CompletableFuture<>();
		
		start = System.currentTimeMillis();
		
		streaming.chat(
				
				question,	//question
				
				new StreamingChatResponseHandler() {
					
					int counter=0;
					
					
					//It represents each chunk getting received from the LLM
					@Override
					public void onPartialResponse(PartialResponse partialResponse, PartialResponseContext context) {
						
						System.out.print(partialResponse.text());
						
						response.append(partialResponse.text());
						counter++;
						
						if(System.currentTimeMillis() - start >4000) {
							
							System.out.println("\n Its 4 seconds since start and its time for manually cancelling the response");
							
							context.streamingHandle().cancel();	//Manually cancelling the stream response
							
							streamResponse.complete(false);
							
							//throw new TimeoutException("sample");		//For recreating the exception case
						}
						
					}
					
					@Override
					public void onError(Throwable error) {
						
						System.out.println("Inside onError method, Some error occurred please check after some time");
						streamResponse.complete(false);
						
					}
					
					@Override
					public void onCompleteResponse(ChatResponse completeResponse) {
						
						System.out.println("\nInside onCompleteResponse method, successfully completed!!");
						System.out.println("Total tokens : "+counter);
						
						streamResponse.complete(true);
					}
				}
		);
		
		
		Boolean finished = streamResponse.join();
        
        System.out.println("\nReceived before the break : " + response.length() + " characters");
        System.out.println("Answer complete?          : " + finished);

        if (finished) {
            return;
        }
        
        System.out.println("===retry===");

        CompletableFuture<ChatResponse> retryResponse = new CompletableFuture<>();
        
        streaming.chat(
      		  List.of(
      				  UserMessage.from(question),
    				  AiMessage.from(response.toString()),
    				  UserMessage.from("Continue exactly from where you stopped. Do not repeat anything.")
    			),
      		  	
      		  new StreamingChatResponseHandler()
      		  {
      	
      	 		@Override
      	 		public void onPartialResponse(String partialResponse) {
      	 		
      	 					System.out.print(partialResponse);
      	 			}

					@Override
					public void onCompleteResponse(ChatResponse completeResponse)
					{
						retryResponse.complete(completeResponse);
						
					}

					@Override
					public void onError(Throwable error)
					{
						
						retryResponse.completeExceptionally(error);
					}
      		  }
      		
      	);
		
        retryResponse.join();
        System.out.println("\n\nRetry completed successfully..");
	}
	
	
}

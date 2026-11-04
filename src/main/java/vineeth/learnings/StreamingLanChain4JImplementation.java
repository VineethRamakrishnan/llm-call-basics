package vineeth.learnings;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.TokenUsage;
import vineeth.learnings.constants.OpenAIModelList;
import static vineeth.learnings.constants.StringConstants.*;

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
	
	public static void main(String[] args) {
		
		streamResponseImplementation();

	}
	
	static void streamResponseImplementation() {
		
		CompletableFuture<ChatResponse> streamResponse = new CompletableFuture<>();
				
		//full implementation of streaming implementation in LC4J
		streaming.chat(
				List.of(
						SystemMessage.from("You're a Devops trainer, explain the concepts with an example"),
						UserMessage.from("Explain kubernets in simple terms within 200 words")
				),
				
				new StreamingChatResponseHandler() {
					
					int counter =0;
					
					//It represents each chunk getting received from the LLM
					@Override
					public void onPartialResponse(String partialResponse) {
						
						System.out.print(partialResponse);
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

}

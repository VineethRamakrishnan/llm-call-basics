package vineeth.learnings.springai;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class to demonstrate the basic building blocks - finishReason, streaming, etc.
 * @author Dell
 *
 */
@RestController
@RequestMapping("/springai")
public class SpringAIChatController {

		//Vendor specific; Low level of abstraction
		private OpenAiChatModel chatModel;
		
		//Vendor agnostic; High level of abstraction
		private ChatClient chatClient;
		
		//Constructor Injection
		public SpringAIChatController(OpenAiChatModel chatModel) {
			this.chatModel = chatModel;
			this.chatClient = ChatClient.builder(chatModel).build();
		}
		
		@GetMapping("/chatModel")
		public String chatWithModel(@RequestParam String query) {
			return chatModel.call(query);
		}
		
		@GetMapping("/chat-metadata")
		public UsageMetaData getMetaData(@RequestParam String query) {
			
			//Getting response from the chatClient
			@Nullable
			ChatResponse response = chatClient.prompt()
							.system("You are Java expert, focus only on Java related queries and deny other queries, Also limit your responses with max 100 words.")
							.user(query)
							.options(OpenAiChatOptions.builder()
									.maxCompletionTokens(50))
							.call()
							.chatResponse();
			
			String aiResponse = response.getResult().getOutput().getText();
			
			ChatResponseMetadata metadata = response.getMetadata();
			Usage usage = metadata.getUsage();
			 
			//Using the record for filling up the data
			 return new UsageMetaData(
					 aiResponse,
					 metadata.getModel(),
					 response.getResult().getMetadata().getFinishReason(),
					 usage.getPromptTokens(), 
					 usage.getCompletionTokens(),
					 usage.getTotalTokens());
		}
		
		
		public record UsageMetaData(
				String aiAnswer, 
				String model,
				String finishReason,
				Integer inputTokens,
				Integer outputTokens,
				Integer totalTokens
				) {}
}



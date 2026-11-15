package vineeth.learnings;


import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;

import vineeth.learnings.constants.OpenAIModelList;
import vineeth.learnings.model.Profile;

/**
 * To map the given data to the Java object.
 * 
 * NOTE: Langchain4J and Spring AI dependencies coliding with the implementation. 
 * So, before running this project please remove or comment them
 * @author Dell
 *
 */
public class StructuredOutputJavaImplementation {

	static OpenAIClient client = OpenAIOkHttpClient.fromEnv();
	
	
	public static void main(String[] args) {
		
		String systemMessage = "\"Read the user message and return the details as JSON.";
		
		String query1 = """
				I'm candidate applying for Tech lead position,
				I'm Vineeth from Chennai. I'm learnt man and having around 10 years of experience mainly in Backend development 
				involving Java, SpringBoot, Microservices tech.
				You can find my development work here https://github.com/VineethRamakrishnan
				""";
		
		StructuredChatCompletionCreateParams<Profile> params = ChatCompletionCreateParams.builder()
												.model(OpenAIModelList.MiniModel)
												.addSystemMessage(systemMessage)
												.addUserMessage(query1)
												.responseFormat(Profile.class)
												.build();
		
		
		//Making call and getting the response
		Profile profile = client
							.chat()
							.completions()
							.create(params)
							.choices()
							.get(0)
							.message()
							.content()
							.orElseThrow();
		
		System.out.println("====Matched profiel====");
		System.out.println(profile);

	}

}

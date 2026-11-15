package vineeth.learnings.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonClassDescription("Profile's particulars including their bio and other information")
public record Profile (

		@JsonPropertyDescription("name of the profile which may or mighn't include last name")
		String name, 
		
		@JsonPropertyDescription("Gender of the profile")
		String gender,
		
		@JsonPropertyDescription("Github repository link which may or maynot present in the profile")
		String githubLink,
		
		@JsonPropertyDescription("Total years of experience of the candidate profile")
		String experience,
		
		@JsonPropertyDescription("The profile's primary skills")
		String skillSet,
		
		@JsonPropertyDescription("The candidate's base location")
		String location

) {}

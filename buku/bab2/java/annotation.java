@JsonSerializable
public class Person {
    @JsonElement
    private String firstName;
    @JsonElement
    private String lastName;
    @JsonElement(key = "personAge")
    private String age;
    @JsonElement
    private String address;
}


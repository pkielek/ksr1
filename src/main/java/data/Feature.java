package data;

import lombok.Getter;
import lombok.Setter;

public class Feature {
    @Getter @Setter
    private Boolean isTextFeature;
    @Getter @Setter
    private String textValue;
    @Getter @Setter
    private Double doubleValue;

    public Feature(Object value, boolean isTextFeature) {
        this.isTextFeature = isTextFeature;
        textValue = isTextFeature ? value.toString() : null;
        doubleValue = isTextFeature ? null : Double.parseDouble(value.toString());
        }
}

package a2.common.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Weinan Qiu
 * @since 1.0.0
 */
public abstract class ValidationResult<T> {

    protected T form;
    protected boolean isValid = false;
    protected Map<String, String> messages = new HashMap<String, String>();

    public T getForm() {
        return form;
    }

    public void setForm(T form) {
        this.form = form;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
}

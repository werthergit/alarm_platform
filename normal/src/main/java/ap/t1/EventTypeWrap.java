package ap.t1;


/**
 * @author
 * @data 2020-12-17 17:14
 * @description
 */
public class EventTypeWrap<E extends Enum> {

    private E[] eventTypes;

    public EventTypeWrap(Class<E> clazz) {
        eventTypes = clazz.getEnumConstants();
        //Assert.notEmpty(eventTypes, "事件类型列表为空！");
    }

    public E[] getEventTypes() {
        return eventTypes;
    }

    public EventTypeWrap<E> setEventTypes(E[] eventTypes) {
        this.eventTypes = eventTypes;
        return this;
    }
}


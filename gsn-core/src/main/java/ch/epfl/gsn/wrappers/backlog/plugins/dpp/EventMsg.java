package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import ch.epfl.gsn.beans.DataField;

public class EventMsg extends AbstractMsg {

	private static DataField[] dataField = {
			new DataField("COMPONENT_ID", "SMALLINT"), /* component id */
			new DataField("TYPE", "SMALLINT"), /* event type / id */
			new DataField("VALUE", "BIGINT") /* event value / subtype */
	};

	@Override
	public Serializable[] receivePayload(ByteBuffer payload) throws Exception {
		Short component_id = null;
		Short type = null;
		Long value = null;

		try {
			type = convertUINT8(payload);
			component_id = convertUINT8(payload);
			value = convertUINT32(payload);
		} catch (Exception e) {
		}

		return new Serializable[] { component_id, type, value };
	}

	@Override
	public DataField[] getOutputFormat() {
		return dataField;
	}

	@Override
	public int getType() {
		return ch.epfl.gsn.wrappers.backlog.plugins.dpp.MessageTypes.DPP_MSG_TYPE_EVENT;
	}

	@Override
	public ByteBuffer sendPayload(String action, String[] paramNames, Object[] paramValues) throws Exception {
        // Implement your logic to construct the payload based on action, paramNames, and paramValues
        // Example logic: concatenate action, paramNames, and paramValues into a ByteBuffer

        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append(action);

        for (int i = 0; i < paramNames.length; i++) {
            payloadBuilder.append(paramNames[i]);
            payloadBuilder.append(String.valueOf(paramValues[i])); // Convert Object to String
        }

        String payloadString = payloadBuilder.toString();
        return ByteBuffer.wrap(payloadString.getBytes(StandardCharsets.UTF_8));
    }

}

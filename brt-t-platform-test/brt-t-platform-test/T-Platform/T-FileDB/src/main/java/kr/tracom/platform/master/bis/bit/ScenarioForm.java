package kr.tracom.platform.master.bis.bit;

import kr.tracom.platform.master.base.BaseModel;
import kr.tracom.platform.net.util.ByteHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class ScenarioForm implements BaseModel {
    public static final int SIZE = 2;

    private byte formType;
    private byte formCount;

    private List<ScenarioItem> items;

    public ScenarioForm() {
        formCount = 0;
        items = new ArrayList<ScenarioItem>();
    }

    public void addItem(ScenarioItem scenarioItem) {
        this.items.add(scenarioItem);
        //this.formCount++;
    }

    @Override
    public int getSize() {
        return SIZE + (ScenarioItem.SIZE * items.size());
    }

    @Override
    public void encode(ByteHelper byteHelper) {
        byteHelper.setByte(this.formType);
        byteHelper.setByte(this.formCount);

        for(ScenarioItem item : items) {
            item.encode(byteHelper);
        }
    }

    @Override
    public void decode(ByteHelper byteHelper) {
        this.formType = byteHelper.getByte();
        this.formCount = byteHelper.getByte();

        short loop = ByteHelper.unsigned(this.formCount);
        for(short i=0; i< loop; i++) {
            ScenarioItem item = new ScenarioItem();
            item.decode(byteHelper);

            items.add(item);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}

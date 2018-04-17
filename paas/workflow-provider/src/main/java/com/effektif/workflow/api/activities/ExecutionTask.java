package com.effektif.workflow.api.activities;

import com.effektif.workflow.api.bpmn.BpmnElement;
import com.effektif.workflow.api.json.TypeName;
import com.effektif.workflow.api.workflow.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * zhenghaibo
 * 18/4/8 16:56
 */
@TypeName("executionTask")
@BpmnElement("executionTask")
public class ExecutionTask extends Activity {

    private static final long serialVersionUID = 6109121899856655719L;

    private List<ExecutionItem> itemList;

    public List<ExecutionItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<ExecutionItem> itemList) {
        this.itemList = itemList;
    }

    public ExecutionTask item(ExecutionItem item) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.add(item);
        return this;
    }
}

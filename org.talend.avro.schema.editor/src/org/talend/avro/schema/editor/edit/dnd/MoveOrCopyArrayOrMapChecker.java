package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.TargetPosition;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Implementation of the {@link DragAndDropPolicy.Checker} for an array or map node. 
 * 
 * @author timbault
 *
 */
public class MoveOrCopyArrayOrMapChecker implements DragAndDropPolicy.Checker {

	@Override
	public boolean accept(AvroNode sourceNode, AvroNode targetNode, TargetPosition position) {

		NodeType targetType = targetNode.getType();
		
		switch (targetType) {
		case FIELD:
		case MAP:
		case ARRAY:
			// these cases need some more validation
			if (targetNode.hasChildren()) {
				AvroNode targetChildNode = targetNode.getChild(0);
				NodeType targetChildType = targetChildNode.getType();
				if (targetChildType == NodeType.UNION) {
					UnionNode unionNode = (UnionNode) targetChildNode;
					if (AttributeUtil.isChoiceType(unionNode)) {
						// the drop is only possible if the choice node has not array/map child
						return !unionNode.hasChildren(sourceNode.getType());
					} else {
						// simple optional case
						AvroNode notNullChild = ModelUtil.getFirstNotNullChild(unionNode);
						// this not null child must be a primitive type one
						return notNullChild.getType() == NodeType.PRIMITIVE_TYPE;
					}
				} else {
					return false;
				}
			} else {
				return true;
			}
		case UNION:
			UnionNode unionNode = (UnionNode) targetNode;
			if (AttributeUtil.isChoiceType(unionNode)) {
				// the drop is only possible if the choice node has not array/map child
				return !unionNode.hasChildren(sourceNode.getType());
			} else {
				return false;
			}
		default:
			return false;
		}
		
	}

}

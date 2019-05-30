package smartspace.infra;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import smartspace.dao.EnhancedActionDao;
import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.ActionEntity;

@Service
public class RESTActionServiceImpl implements RESTActionService {
	private EnhancedActionDao actionDao;
	private EnhancedUserDao<String> userDao;
	private EnhancedElementDao<String> elementDao;
	private String smartspaceName;
	
	@Autowired
	public RESTActionServiceImpl(EnhancedActionDao actionDao) {
		this.actionDao = actionDao;
	}

	@Value("${smartspace.name}")
	public void setRESTActionSmartspaceName(String smartspaceName) {
		this.smartspaceName = smartspaceName;
	}
	
	@Override
	public ActionEntity invokeAction(ActionEntity newAction) {
		try {
			String actionType = newAction.getActionType();
			if (actionType != null && !actionType.trim().isEmpty()) {
				if (valiadate(newAction)) {
					newAction.setCreationTimestamp(new Date());
					return this.actionDao.create(newAction);
				} else {
					throw new RuntimeException("Action is invalid - Wrong element of user");
				}
			} else {
				throw new RuntimeException("Illegal action type");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean valiadate(ActionEntity entity) {
		if (entity.getPlayerEmail() != null && entity.getPlayerSmartspace() != null && entity.getElementId() != null
				&& entity.getElementSmartspace() != null && entity.getActionType() != null) {
			if (this.userDao.readById(entity.getPlayerEmail() + "=" + entity.getPlayerSmartspace()).isPresent()
					&& this.elementDao.readById(entity.getElementId() + "=" + entity.getElementSmartspace())
							.isPresent()) {
				return true;
			}
		}
		return false;
	}

	
	/*
	@Override
	public MessageEntity play(MessageEntity message) {
		if (message.getName() == null ||
			message.getName().trim().isEmpty()) {
			throw new IllegalNameException();
		}
		
		if (message.getSeverity() == null) {
			throw new RuntimeException();
		}
		
		try {
			String name = message.getName();
//		"guess" ------>>>>>>> smartspace.plugin.GuessPlugin
			String className = 
					"smartspace.plugin." 
					+ name.toUpperCase().charAt(0) 
					+ name.substring(1, name.length())
					+ "Plugin";
			Class<?> theClass = Class.forName(className);
			Plugin plugin = (Plugin) this.ctx.getBean(theClass);
			
			message.setAvailableFrom(new Date());
			message = plugin.process(message);
			
			this.messageDao.create(message);
			return message;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	} 
	 */
}

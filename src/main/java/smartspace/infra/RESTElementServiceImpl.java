package smartspace.infra;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.ElementBoundary;
import smartspace.layout.data.ElementKeyBoundary;

@Service
public class RESTElementServiceImpl implements RESTElementService {

	private EnhancedElementDao<String> elementDao;
	private EnhancedUserDao<String> userDao;
	private String smartspaceName;
	
	@Autowired
	public void setElementDao(EnhancedElementDao<String> elementDao) {
		this.elementDao = elementDao;
	}

	@Override
	public void updateElement(ElementEntity elementEntity, String managerSmartspace, String managerEmail,
			String elementSmartspace, String elementId) {

		String key = managerSmartspace + "=" + managerEmail;
		UserEntity userEntity = userDao.readById(key).orElseThrow(() -> new RuntimeException("user doesn't exist"));

		if (!userEntity.getRole().equals(UserRole.MANAGER)) {
			throw new RuntimeException("you are not MANAGER");
		}

		elementDao.update(elementEntity);
	}

	@Override
	public ElementEntity createNewElement(ElementEntity elementEntity, String managerSmartspace, String managerEmail) {

		String key = managerSmartspace + "=" + managerEmail;
		ElementEntity newElementEntity = elementDao.readById(key)
				.orElseThrow(() -> new RuntimeException("element doesn't exist"));

//		if (!newElementEntity.getElementSmartspace().equals(managerSmartspace)) { // not sure about the check
//			throw new RuntimeException("you are not MANAGER"); //
//		}

		return elementDao.create(newElementEntity);

	}

	@Override
	public List<ElementEntity> getUsingPagination(int size, int page, String userSmartspace, String userEmail) {

		String key = userSmartspace + "=" + userEmail;

		ElementEntity entity = elementDao.readById(key)
				.orElseThrow(() -> new RuntimeException("element doesn't exist"));

//		if (entity.getRole() != UserRole.ADMIN) // not sure again check
//			throw new RuntimeException("only the admin is allowed to create users");

		return this.elementDao.readAll("key", size, page);

	}

	@Override
	public ElementEntity findById(String elementSmartspace, String elementId) {
		return this.elementDao
				.readById(elementSmartspace + "=" + elementId)
				.orElseThrow(() -> new RuntimeException("Element not found!"));
	}
	
	@Override
	public List<ElementEntity> findNearLocation(String userSmartspace,String userEmail,
			String search,double x,double y,double distance,int page,int size){
			return this.elementDao.readAllByDistanceFromLocation
				(new Location(x, y), distance, size, page);
		
	}

}
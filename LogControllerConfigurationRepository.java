import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogControllerConfigurationRepository extends MongoRepository<LogControllerConfiguration, String> {

}
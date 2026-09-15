# FoodReels backend validation

## Scope and test environment

The suite runs the application with `@SpringBootTest`, `@AutoConfigureMockMvc`, `@ActiveProfiles("test")`, real controllers/services/JPA repositories, and transactional test fixtures. Each test creates its own users, restaurants and foods; generated IDs are used in requests. Database assertions flush and clear the persistence context where persisted values matter.

The existing Spring Boot 4.1.0 dependency management resolves JUnit Jupiter **6.0.3** (the familiar Jupiter test API), with MockMvc, AssertJ and Spring Security Test. No Java or Spring version was changed. The existing Boot test starters already provide the general testing tools; only test-scoped `spring-security-test` and H2 were added.

H2 runs in PostgreSQL compatibility mode. Flyway and the gRPC server are disabled only in the test profile. A test bean replaces the explicitly configured Redis cache manager with `NoOpCacheManager`; the direct Redis eviction service is mocked. Production Redis and Flyway remain enabled and their implementations are retained.

Most authenticated requests use MockMvc `jwt()` with the actual `ROLE_USER`, `ROLE_RESTAURANT_OWNER` and `ROLE_ADMIN` authorities. Additional tests use the production JWT encoder/decoder to check role conversion and expiration.

## Coverage and counts

| Group | Test classes | Test methods | Executed cases |
| --- | ---: | ---: | ---: |
| Automated/backend, including context | 2 | 16 | 16 |
| Security | 1 | 15 | 15 |
| Orders | 1 | 18 | 21 |
| Location | 1 | 6 | 15 |
| Search | 1 | 12 | 27 |
| Recommendations | 1 | 10 | 32 |
| **Total** | **7** | **77** | **126** |

**76 test methods were added**, producing **125 new executed cases**, alongside the retained context test. Parameterized invocations explain the difference between methods and cases. No numerical code-coverage percentage was measured.

- **Backend:** public registration/login, password encoding, valid and missing enum roles, invalid credentials, duplicate registration, validation envelopes, missing resources, malformed JSON, invalid pagination/status types.
- **Security:** anonymous and malformed/expired tokens; real role conversion; USER restrictions; owner access; cross-owner order listing/status updates and restaurant update/delete rejection; ownerless restaurant rejection; admin access and management; standardized 401/403 JSON including path.
- **Orders:** authenticated ownership; restaurant derivation; database prices and persisted snapshots; client price/owner tampering; quantities, subtotals and totals; duplicate-item merging; mixed-restaurant and invalid requests; history isolation, pagination and newest-first order; status filtering; customer ownership/cancellation; complete delivery workflow; invalid and terminal transitions; restaurant filtering/pagination.
- **Location:** nearby inclusion, far exclusion, exclusion outside the circular radius despite lying within the bounding box, nearest-first ordering, food/reel restaurant distance, combined results, invalid coordinate/radius limits and required parameters.
- **Search:** food name/description, restaurant name/address, reel caption, category/relationship/price/rating filters, pagination, all implemented sort choices, empty results, unified results and relevance within the fetched page.
- **Recommendations:** preference +6, watch +1, like +3, comment +4, save +5; user-specific debug/profile data; recency/popularity/watch-penalty thresholds; ranking, timestamp/ID tie breaks, cold-start newest-first behavior and pagination.

## Confirmed defects and smallest fixes

Each functional fix has a regression test that failed before the fix.

| Defect observed | Production files changed | Result |
| --- | --- | --- |
| An owner could list or update another restaurant's orders | `service/OrderService.java` | Check the authenticated restaurant owner before listing or changing status; retain admin override |
| Security failures omitted the request path | `config/SecurityConfig.java` | Serialize the existing `ApiErrorResponse` for 401/403, including timestamp and path |
| Missing foods, reels and users returned 500 | `exception/GlobalExceptionHandler.java` | Map existing not-found exceptions to standardized 404 responses |
| Wrong passwords and unknown login emails returned 500 | `service/AuthService.java`, `exception/GlobalExceptionHandler.java` | Use the existing invalid-credentials exception consistently and return 401 |
| Duplicate registration returned 500 | `exception/GlobalExceptionHandler.java` | Map the existing duplicate-email exception to 409 |
| `@NotBlank` on the role enum broke valid and invalid admin user creation | `dto/UserRequestDTO.java` | Use `@NotNull` for the enum |
| Admin user creation/updates stored raw passwords and broke BCrypt verification | `service/UserService.java` | Inject the existing password encoder and encode both writes |

Paths in this table are relative to `src/main/java/com/foodreels/backend/`.

## Files created

Under `src/test/java/com/foodreels/backend/`:

- `support/BackendIntegrationTest.java`
- `integration/BackendValidationTests.java`
- `integration/OrderIntegrationTests.java`
- `integration/LocationIntegrationTests.java`
- `integration/SearchIntegrationTests.java`
- `integration/RecommendationIntegrationTests.java`
- `security/SecurityRegressionTests.java`

Also created:

- `src/test/resources/application-test.properties`
- `docs/backend-validation.md` (this report)

## Files modified in this validation task

- `pom.xml`: two test-only dependencies.
- `src/test/java/com/foodreels/backend/FoodreelsBackendApplicationTests.java`: retain `contextLoads()` and use the isolated test environment.
- The six production Java files listed in the defect table.
- `controller/ReelController.java`: remove two duplicate imports.
- `service/LocationService.java`, `service/ReelService.java`, `service/SearchService.java`, `service/RecommendationService.java`, `service/PersonalizedFeedCacheService.java`: remove temporary console output; remove the obsolete comment explaining the removed recommendation console message.
- `src/main/resources/application.properties`: replace the literal database password with `${DB_PASSWORD}`.

The controller documentation changes from the preceding task were already present. Their Swagger descriptions were preserved. Aside from duplicate imports in `ReelController`, this task did not modify controllers. Eight duplicate imports were also removed from `SecurityConfig`. No duplicate repository imports were found.

## Configuration and safety review

The tracked database password was replaced with an environment placeholder. **Set `DB_PASSWORD` before running the application**, together with the existing required `JWT_SECRET`. The test profile uses only test credentials and a test-only signing key. Actual credential values are omitted from this report. Removing the password from the working source does not remove it from earlier Git history; rotate the previously tracked credential if it is still in use.

No endpoint mappings, entities, repositories, migration files or production caching annotations changed. No Docker, Compose, or Testcontainers files/dependencies were added. Production PostgreSQL settings remain in place; the password change is credential cleanup, not a test database substitution.

## Verification

- `mvnw.cmd clean test`: **BUILD SUCCESS**, 126 cases, zero failures/errors/skips.
- `mvnw.cmd clean package`: **BUILD SUCCESS**, all 126 cases passed again; executable JAR generated.
- A final incremental `mvnw.cmd -DskipTests package` check explicitly captured native Maven exit code **0** after PowerShell reported a nonzero shell status for the redirected full runs. The preceding clean package run executed all 126 tests successfully.
- Endpoint mapping and production caching-annotation preservation checks passed; `git diff --check` passed. The packaged JAR excludes H2 and test resources.

## Limits

All requested behavior categories have implemented tests. This is an H2 integration suite, not a live PostgreSQL/Redis deployment test. PostgreSQL-specific migration DDL and Redis connectivity, serialization and expiration were not exercised: Flyway is disabled for the H2 schema and cache dependencies are replaced for isolated execution. The suite does not establish global search relevance beyond the implementation's fetched-page ranking. Existing production database contents were not migrated or repaired; password hashing applies to subsequent create/update operations. The application was not deployed.

## FINAL DOCUMENTATION FACTS

- Framework: JUnit Jupiter 6.0.3, Spring Boot Test, MockMvc, AssertJ, Spring Security Test.
- Approach: full Spring application context with H2, real JPA queries, generated fixtures and transaction rollback; test-only cache replacements.
- Authentication: MockMvc JWT authorities plus real signed/expired test tokens using the application's JWT components.
- Size: 7 test classes, 77 test methods, 126 executed cases; 76 methods/125 cases added.
- Modules verified: authentication, users, security, orders, location, search, recommendation, and interactions used by recommendation profiles.
- Security verified: role restrictions, restaurant ownership, admin override, invalid/expired tokens, standardized errors and password encoding.
- Order, location, search and recommendation scenarios are listed in the coverage section above.
- Final package status: **BUILD SUCCESS**; executable artifact is `target/foodreels-backend-0.0.1-SNAPSHOT.jar`.

package com.example.demo.service;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MicroStackService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String microStackHost = "https://34.27.147.38";
    private final String memberRoleId = "f3286375e10842828ecf7642de83ffdc"; //That's the admin one instead, the admin role
    private final String networkId = "ab171b4e-fb14-4c0a-bc1d-1e1a6440bcc1";
    private final String keystoneUrl = microStackHost + ":5000";
    private final String neutronUrl = microStackHost + ":9696";
    private final String novaUrl = microStackHost + ":8774";
    private final String adminToken = "gAAAAABnvuraGqXuYzajkWLDMNR7WcKaGwPqnPDQ0WhhGvT05lYHnjQvcb2FeV_VwcSKQ0ZH9TTIWauNSXxHjcCJc72Cvu65UNcJKwKC-LCBAwDzZDVxirO4aisZwJ6jChsNE8QibYCgNDGHXsjUdp67aquudStUKw"; // Unscoped admin token

    public MicroStackService() throws Exception {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = SSLContexts.custom()
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
            sslContext, NoopHostnameVerifier.INSTANCE
        );
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(
                PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(socketFactory)
                    .build()
            )
            .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        this.restTemplate = new RestTemplate(factory);
    }

    // Create user with admin token
    public String createUser(String username, String password) {
    	    HttpHeaders headers = new HttpHeaders();
    	    headers.set("X-Auth-Token", adminToken);
    	    headers.setContentType(MediaType.APPLICATION_JSON);

    	    Map<String, Object> userMap = Map.of(
    	        "name", username,
    	        "password", password,
    	        "domain_id", "default"
    	    );
    	    Map<String, Object> body = Map.of("user", userMap);

    	    // Log the body
    	    try {
    	        String jsonBody = objectMapper.writeValueAsString(body);
    	        System.out.println("Request Body: " + jsonBody);
    	        headers.setContentLength(jsonBody.getBytes().length); // Explicitly set Content-Length
    	    } catch (Exception e) {
    	        System.err.println("Failed to serialize body: " + e.getMessage());
    	    }

    	    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    	    // Log the full request details
    	    System.out.println("Request URL: " + keystoneUrl + "{\r\n"
    	    		+ "    \"user\": {\r\n"
    	    		+ "        \"name\": \"bgp\",\r\n"
    	    		+ "        \"password\": \"123456789\",\r\n"
    	    		+ "        \"domain_id\": \"default\"\r\n"
    	    		+ "    }\r\n"
    	    		+ "}");
    	    System.out.println("Request Headers: " + headers);

    	    try {
    	        ResponseEntity<String> response = restTemplate.postForEntity(
    	            keystoneUrl + "/v3/users", request, String.class);
    	        System.out.println("Response: " + response.getBody());
    	        return extractIdFromResponse(response.getBody());
    	    } catch (Exception e) {
    	        System.err.println("Request failed: " + e.getMessage());
    	        throw e;
    	    }
    	}
    
    public void assignUserToProject(String userId, String projectId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", adminToken);
        String url = keystoneUrl + "/v3/projects/" + projectId + "/users/" + userId + "/roles/" + memberRoleId;
        try {
            restTemplate.put(url, new HttpEntity<>(headers)); // PUT has no body
            System.out.println("Assigned user " + userId + " to project " + projectId + " with role " + memberRoleId);
        } catch (Exception e) {
            System.err.println("Role assignment failed: " + e.getMessage());
            throw e;
        }
    }

    // Create project for the user with admin token
    public String createProject(String projectName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
	    Map<String, Object> projectMap = Map.of(
    	        "name", projectName,
    	        "domain_id", "default",
                "enabled", true
    	    );
    	Map<String, Object> body = Map.of("project", projectMap);        
        try {
	        String jsonBody = objectMapper.writeValueAsString(body);
	        System.out.println("Request Body: " + jsonBody);
	        headers.setContentLength(jsonBody.getBytes().length); // Explicitly set Content-Length
	    } catch (Exception e) {
	        System.err.println("Failed to serialize body: " + e.getMessage());
	    }

	    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

	    // Log the full request details
	    System.out.println("Request URL: " + keystoneUrl + "/v3/projects");
	    System.out.println("Request Headers: " + headers);

	    try {
	        ResponseEntity<String> response = restTemplate.postForEntity(
	            keystoneUrl + "/v3/projects", request, String.class);
	        System.out.println("Response: " + response.getBody());
	        return extractIdFromResponse(response.getBody());
	    } catch (Exception e) {
	        System.err.println("Request failed: " + e.getMessage());
	        throw e;
	    }  
    }

    // Get scoped token for the user
    public String getUserScopedToken(String username, String password, String projectId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        System.out.println("username : "+username+" pwd : "+password+" project : "+projectId);
        Map<String, Object> body = Map.of(
            "auth", Map.of(
                "identity", Map.of(
                    "methods", List.of("password"),
                    "password", Map.of(
                        "user", Map.of(
                            "name", username,
                            "domain", Map.of("name", "default"),
                            "password", password
                        )
                    )
                ),
                "scope", Map.of(
                    "project", Map.of(
                        "id", projectId,
                        "domain", Map.of("name", "default")
                    )
                )
            )
        );
        
        try {
	        String jsonBody = objectMapper.writeValueAsString(body);
	        System.out.println("Request Body: " + jsonBody);
	        headers.setContentLength(jsonBody.getBytes().length); // Explicitly set Content-Length
	    } catch (Exception e) {
	        System.err.println("Failed to serialize body: " + e.getMessage());
	    }

	    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

	    // Log the full request details
	    System.out.println("Request URL: " + keystoneUrl + "/v3/auth/tokens");
	    System.out.println("Request Headers: " + headers);

	    try {
	        ResponseEntity<String> response = restTemplate.postForEntity(
	            keystoneUrl + "/v3/auth/tokens", request, String.class);
	        String token = response.getHeaders().getFirst("X-Subject-Token"); // Get token from header
	        System.out.println("Response Body: " + response.getBody());
	        System.out.println("Scoped Token: " + token);
	        return token; // Return the header token
	    } catch (Exception e) {
	        System.err.println("Request failed: " + e.getMessage());
	        throw e;
	    } 
    }

    // Create subnet with user-scoped token
    public String createSubnet(String username, String password, String projectId) {
        String userToken = getUserScopedToken(username, password, projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
	    Map<String, Object> subnetMap = Map.of(
                "cidr", "192.168." + username.hashCode() % 255 + ".0/24",
                "ip_version", 4,
                "network_id", networkId
    	    );
    	    Map<String, Object> body = Map.of("subnet", subnetMap);
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            System.out.println("Request Body: " + jsonBody);
            headers.setContentLength(jsonBody.getBytes().length);
        } catch (Exception e) {
            System.err.println("Failed to serialize body: " + e.getMessage());
        }
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                neutronUrl + "/v2.0/subnets", request, String.class);
            System.out.println("Response: " + response.getBody());
            return extractIdFromResponse(response.getBody());
        } catch (Exception e) {
            System.err.println("Subnet creation failed: " + e.getMessage());
            throw e;
        }
    }

    // Create VM with user-scoped token
    public String createVm(String username, String password, String projectId, String subnetId, int cpu, int ramMb, int diskGb) {
        String userToken = getUserScopedToken(username, password, projectId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of(
            "server", Map.of(
                "name", "vps-" + System.currentTimeMillis(),
                "flavorRef", getFlavorId(cpu, ramMb, diskGb),
                "imageRef", "7a76b2bb-29c0-4e14-83fa-20c06207347f"
            )
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                novaUrl + "/v2.1/servers", request, String.class);
            String vmId = extractIdFromResponse(response.getBody());
            return getVmDetails(vmId, userToken);
        } catch (Exception e) {
            System.err.println("VM creation failed: " + e.getMessage());
            throw e;
        }
    }

    public String extractIdFromResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            if (root.has("user")) {
                return root.get("user").get("id").asText();
            } else if (root.has("subnet")) {
                return root.get("subnet").get("id").asText();
            } else if (root.has("server")) {
                return root.get("server").get("id").asText();
            } else if (root.has("project")) {
                return root.get("project").get("id").asText();
            }
            throw new RuntimeException("ID not found in response: " + jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ID from response: " + jsonResponse, e);
        }
    }

    public String extractVmId(String jsonResponse) {
        return extractIdFromResponse(jsonResponse);
    }

    public String extractIp(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode addresses = root.get("server").get("addresses");
            return addresses.elements().next().get(0).get("addr").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract IP: " + jsonResponse, e);
        }
    }

    public String extractPassword(String jsonResponse) {
        return "default-password"; // Placeholder
    }

    public String getFlavorId(int cpu, int ramMb, int diskGb) {
        if (cpu == 1 && ramMb <= 1024 && diskGb <= 20) {
            return "1";
        } else if (cpu == 2 && ramMb <= 2048 && diskGb <= 40) {
            return "2";
        } else if (cpu == 4 && ramMb <= 4096 && diskGb <= 80) {
            return "3";
        } else {
            throw new IllegalArgumentException("No flavor matches the requested specs");
        }
    }

    public String getVmDetails(String vmId, String userToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", userToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                novaUrl + "/v2.1/servers/" + vmId, HttpMethod.GET, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("VM details retrieval failed: " + e.getMessage());
            throw e;
        }
    }
}
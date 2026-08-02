# Internal MCP Server

A safe Java 17/Spring Boot 4 MCP starter for internal customer and order workflows. It exposes a **Streamable HTTP** endpoint at `/mcp`, suitable for deployment and ChatGPT custom connectors.

## What it exposes

- `search_customers(query, limit)`
- `get_customer(customerId)`
- `create_order(customerId, sku, quantity, unitPrice)` — creates a `PENDING_REVIEW` record only; it never charges, fulfills, or submits an order.
- `run_health_check()`
- `internal://service-info` resource

The included data is in-memory demo data. Replace `InternalStore` with your authenticated database/API adapter before using real customer data.

## Run locally

```bash
mvn test
mvn package
java -jar target/internal-mcp-server-0.1.0.jar
```

The local MCP endpoint is `http://localhost:8080/mcp`; health checks are available at `http://localhost:8080/actuator/health`.

## Container image

Build and run it as a container:

```bash
mvn package
docker build -t internal-mcp-server:0.1.0 .
docker run --rm -p 8080:8080 internal-mcp-server:0.1.0
```
## Verify the tools

1. Install Node in Mac. "brew install node"

2. If you already have npm but missing npx, install it globally via "npm install -g npx"

3. Run "npx @modelcontextprotocol/inspector"

4. Copy similar URL with auth token from the terminal:

   http://localhost:6274?MCP_INSPECTOR_API_TOKEN=ef58ea73bcf6b6849d97576c0c8cdc0525f6699429064700351b4edc804ed558
    
5. Put above URL into the browser, you can verify the tools in your MCP server.

## Test local deployment for MCP server (Mac)
1) Install ngrok, by brew install --cask ngrok
2) Register an account in ngrok and create a token in ngrok account
3) Run ngrok config add-authtoken <the token created in ngrok account>
4) Run ngrok http "server port" (ex: ngrok http 8080), which is the server port of you MCP server
   You will see https://overlook-kitten-primate.ngrok-free.dev -> http://localhost:8080, which first url will be used to setup
   MCP server in ChatGPT
5) Enable developer mode in ChatGPT
6) Go to Plugins in ChatGPT, click + symbol to add MCP server locally
7) Use first URL as connection string with your MCP server endpoint.
8) Create new chat, using this MCP to verify the functions provided by this MCP server
9) ChatGP seems caching MCP server. Sometimes it needs to be deleted and re-installed to see new features.

To test it in Claude, run step 4. Add connectors under the setting by the mapping URL, not the URL with Localhost. 

## Connect it to ChatGPT

1. Deploy the container to a host with a public HTTPS URL (for example Cloud Run, AWS App Runner, Render, or Azure Container Apps).
2. Verify `https://your-domain.example/mcp` is reachable and that `https://your-domain.example/actuator/health` reports `UP`.
3. In ChatGPT web, enable Developer Mode if your workspace provides it, then create a custom MCP connector and enter `https://your-domain.example/mcp`.
4. Test the connector in a new chat with: `Search customers for Avery`.

Do **not** expose this demo service publicly with real data. Before deployment, add OAuth or an API gateway, enforce authorization per customer/order, and replace `InternalStore` with least-privilege authenticated integrations.

## Production checklist

1. Replace demo storage with least-privilege authenticated adapters.
2. Add user identity/authorization checks before every lookup or mutation.
3. Audit each tool invocation without recording secrets or full sensitive payloads.
4. Keep destructive operations out of broad, ambiguous tools; require explicit confirmation in the client workflow.
5. Use OAuth or an API gateway before exposing a remotely reachable server.

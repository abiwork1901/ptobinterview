import { FormEvent, useState } from "react";

type HealthResponse = {
  status: string;
  groups?: string[];
};

export default function App() {
  const [baseUrl, setBaseUrl] = useState("http://localhost:8080");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState("Click Check Health to verify backend.");

  const checkHealth = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setResult("Checking backend health...");

    try {
      const response = await fetch(`${baseUrl}/actuator/health`);
      const body = (await response.json()) as HealthResponse;
      setResult(
        `Status Code: ${response.status}\n` +
          `Health Status: ${body.status}\n` +
          `Groups: ${(body.groups ?? []).join(", ")}`
      );
    } catch (error) {
      setResult(`Request failed: ${String(error)}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main
      style={{
        maxWidth: 720,
        margin: "2rem auto",
        padding: "1.5rem",
        fontFamily: "Inter, Arial, sans-serif",
      }}
    >
      <h1>PTOB React + TypeScript Client</h1>
      <p>Starter web app to verify omnibus backend connectivity.</p>

      <form onSubmit={checkHealth} style={{ display: "grid", gap: "0.75rem" }}>
        <label htmlFor="baseUrl">Backend Base URL</label>
        <input
          id="baseUrl"
          value={baseUrl}
          onChange={(event) => setBaseUrl(event.target.value)}
          placeholder="http://localhost:8080"
          style={{ padding: "0.5rem" }}
        />
        <button type="submit" disabled={loading} style={{ width: 160, padding: "0.5rem" }}>
          {loading ? "Checking..." : "Check Health"}
        </button>
      </form>

      <pre
        style={{
          marginTop: "1rem",
          padding: "1rem",
          border: "1px solid #ddd",
          borderRadius: 8,
          background: "#fafafa",
          whiteSpace: "pre-wrap",
        }}
      >
        {result}
      </pre>
    </main>
  );
}

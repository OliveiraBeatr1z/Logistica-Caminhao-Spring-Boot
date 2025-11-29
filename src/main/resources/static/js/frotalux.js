// Variável global para armazenar a chave de API.
const apiKey = "";
const apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent";

const promptInput = document.getElementById('promptInput');
const generateButton = document.getElementById('generateButton');
const resultOutput = document.getElementById('resultOutput');
const loadingIndicator = document.getElementById('loadingIndicator');
const sourcesOutput = document.getElementById('sourcesOutput');
const sourcesList = document.getElementById('sourcesList');

// Função utilitária para introduzir atraso (para backoff exponencial)
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// ----------------------------------------------------------------------
// 1. Função de Chamada da API com Backoff Exponencial
// ----------------------------------------------------------------------
async function fetchWithExponentialBackoff(url, options, maxRetries = 5) {
    let delay = 1000;

    for (let i = 0; i < maxRetries; i++) {
        try {
            const response = await fetch(url, options);

            if (response.ok) {
                return response;
            }

            if (response.status === 429 || response.status >= 500) {
                console.warn(`Erro de API (${response.status}). Tentando novamente em ${delay / 1000}s...`);
                await sleep(delay);
                delay *= 2;
                continue;
            }

            const errorDetails = await response.text();
            throw new Error(`Erro de API: ${response.status} - ${errorDetails}`);

        } catch (error) {
            if (i === maxRetries - 1) {
                throw new Error(`Falha no fetch após ${maxRetries} tentativas: ${error.message}`);
            }
            console.error(`Erro de rede. Tentando novamente em ${delay / 1000}s...`);
            await sleep(delay);
            delay *= 2;
        }
    }
}


// ----------------------------------------------------------------------
// 2. Função Principal de Geração de Conteúdo
// ----------------------------------------------------------------------
async function generateContent() {
    const prompt = promptInput.value.trim();
    if (!prompt) {
        resultOutput.textContent = "Por favor, insira um tópico para gerar conteúdo.";
        return;
    }

    // UI Feedback
    generateButton.disabled = true;
    loadingIndicator.classList.remove('hidden');
    resultOutput.textContent = "";
    sourcesOutput.classList.add('hidden');
    sourcesList.innerHTML = "";

    const fullUrl = `${apiUrl}?key=${apiKey}`;

    const systemPrompt = "Você é um assistente de escrita criativa e analítica de classe mundial. Responda à consulta do usuário de forma útil, completa e num tom amigável e profissional.";

    const payload = {
        contents: [{ parts: [{ text: prompt }] }],
        tools: [{ "google_search": {} }],
        systemInstruction: {
            parts: [{ text: systemPrompt }]
        },
    };

    try {
        const response = await fetchWithExponentialBackoff(fullUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const result = await response.json();
        const candidate = result.candidates?.[0];

        if (candidate && candidate.content?.parts?.[0]?.text) {

            // 1. Extrair o Texto Gerado
            const text = candidate.content.parts[0].text;
            resultOutput.textContent = text;

            // 2. Extrair Fontes (Citações)
            let sources = [];
            const groundingMetadata = candidate.groundingMetadata;

            if (groundingMetadata && groundingMetadata.groundingAttributions) {
                sources = groundingMetadata.groundingAttributions
                    .map(attribution => ({
                        uri: attribution.web?.uri,
                        title: attribution.web?.title,
                    }))
                    .filter(source => source.uri && source.title);
            }

            if (sources.length > 0) {
                sourcesOutput.classList.remove('hidden');
                sources.forEach(source => {
                    const li = document.createElement('li');
                    const a = document.createElement('a');
                    a.href = source.uri;
                    a.target = "_blank";
                    a.textContent = source.title;
                    a.classList.add('text-indigo-600', 'hover:text-indigo-800', 'underline');
                    li.appendChild(a);
                    sourcesList.appendChild(li);
                });
            }

        } else {
            resultOutput.textContent = "Erro: A resposta da IA não pôde ser processada. Por favor, tente novamente.";
            console.error("Estrutura de resposta inesperada:", result);
        }

    } catch (error) {
        console.error("Erro durante a chamada da API:", error);
        resultOutput.textContent = `Ocorreu um erro: ${error.message}. Verifique o console para mais detalhes.`;
    } finally {
        // UI Feedback
        generateButton.disabled = false;
        loadingIndicator.classList.add('hidden');
    }
}

// Adicionar o Event Listener ao botão
document.addEventListener('DOMContentLoaded', () => {
    generateButton.addEventListener('click', generateContent);
});
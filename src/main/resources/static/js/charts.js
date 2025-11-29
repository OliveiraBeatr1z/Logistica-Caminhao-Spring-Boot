/**
 * FrotaLux - Charts & Data Visualization
 * Gráficos usando Chart.js
 */

const FrotaCharts = {
    // Cores do tema
    colors: {
        primary: '#1e3a8a',
        primaryLight: '#2563eb',
        secondary: '#64748b',
        success: '#10b981',
        warning: '#f59e0b',
        danger: '#ef4444',
        info: '#3b82f6'
    },

    // Configuração padrão dos gráficos
    defaultOptions: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                display: true,
                position: 'bottom',
                labels: {
                    font: {
                        family: 'Inter',
                        size: 12
                    },
                    padding: 15,
                    usePointStyle: true
                }
            },
            tooltip: {
                backgroundColor: 'rgba(30, 58, 138, 0.9)',
                titleFont: {
                    family: 'Poppins',
                    size: 14
                },
                bodyFont: {
                    family: 'Inter',
                    size: 13
                },
                padding: 12,
                cornerRadius: 8
            }
        }
    },

    // Gráfico de Entregas por Status (Pizza)
    createStatusPieChart(canvasId, data) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return null;

        return new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Coleta', 'Processamento', 'A Caminho', 'Entregue'],
                datasets: [{
                    data: data,
                    backgroundColor: [
                        this.colors.warning,
                        this.colors.info,
                        this.colors.secondary,
                        this.colors.success
                    ],
                    borderWidth: 3,
                    borderColor: '#ffffff',
                    hoverOffset: 10
                }]
            },
            options: {
                ...this.defaultOptions,
                cutout: '60%',
                plugins: {
                    ...this.defaultOptions.plugins,
                    legend: {
                        ...this.defaultOptions.plugins.legend,
                        position: 'right'
                    }
                }
            }
        });
    },

    // Gráfico de Entregas nos Últimos 7 Dias (Linha)
    createWeeklyLineChart(canvasId, labels, data) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return null;

        return new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Entregas',
                    data: data,
                    borderColor: this.colors.primary,
                    backgroundColor: `${this.colors.primary}20`,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                    pointBackgroundColor: this.colors.primary,
                    pointBorderColor: '#ffffff',
                    pointBorderWidth: 2
                }]
            },
            options: {
                ...this.defaultOptions,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1,
                            font: {
                                family: 'Inter'
                            }
                        },
                        grid: {
                            color: '#e2e8f0'
                        }
                    },
                    x: {
                        ticks: {
                            font: {
                                family: 'Inter'
                            }
                        },
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    },

    // Gráfico de Taxa de Ocupação (Barra Horizontal)
    createOccupancyBarChart(canvasId, groups) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return null;

        const labels = groups.map((g, i) => `Grupo ${i + 1}`);
        const data = groups.map(g => g.taxaUtilizacao);

        return new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Taxa de Ocupação (%)',
                    data: data,
                    backgroundColor: data.map(value => {
                        if (value >= 80) return this.colors.success;
                        if (value >= 60) return this.colors.warning;
                        return this.colors.danger;
                    }),
                    borderRadius: 8,
                    borderSkipped: false
                }]
            },
            options: {
                ...this.defaultOptions,
                indexAxis: 'y',
                scales: {
                    x: {
                        beginAtZero: true,
                        max: 100,
                        ticks: {
                            callback: (value) => value + '%',
                            font: {
                                family: 'Inter'
                            }
                        },
                        grid: {
                            color: '#e2e8f0'
                        }
                    },
                    y: {
                        ticks: {
                            font: {
                                family: 'Inter'
                            }
                        },
                        grid: {
                            display: false
                        }
                    }
                },
                plugins: {
                    ...this.defaultOptions.plugins,
                    tooltip: {
                        ...this.defaultOptions.plugins.tooltip,
                        callbacks: {
                            label: (context) => {
                                return `Ocupação: ${context.parsed.x.toFixed(1)}%`;
                            }
                        }
                    }
                }
            }
        });
    },

    // Gráfico de Economia (Barra)
    createSavingsChart(canvasId, groups) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return null;

        const labels = groups.map((g, i) => `Grupo ${i + 1}`);
        const data = groups.map(g => g.economiaEstimada);

        return new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Economia (R$)',
                    data: data,
                    backgroundColor: this.colors.success,
                    borderRadius: 8,
                    borderSkipped: false
                }]
            },
            options: {
                ...this.defaultOptions,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: (value) => 'R$ ' + value.toFixed(2),
                            font: {
                                family: 'Inter'
                            }
                        },
                        grid: {
                            color: '#e2e8f0'
                        }
                    },
                    x: {
                        ticks: {
                            font: {
                                family: 'Inter'
                            }
                        },
                        grid: {
                            display: false
                        }
                    }
                },
                plugins: {
                    ...this.defaultOptions.plugins,
                    tooltip: {
                        ...this.defaultOptions.plugins.tooltip,
                        callbacks: {
                            label: (context) => {
                                return `Economia: R$ ${context.parsed.y.toFixed(2)}`;
                            }
                        }
                    }
                }
            }
        });
    },

    // Gráfico de Manutenções (Linha do Tempo)
    createMaintenanceChart(canvasId, caminhoes, manutencoes) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return null;

        const labels = caminhoes.map(c => c.placa);
        const proximaManutencao = caminhoes.map(c => {
            const ultima = manutencoes.find(m => m.caminhaoId === c.id);
            return ultima ? ultima.proximaManutencaoKm - c.kmAtual : 10000;
        });

        return new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Km até Manutenção',
                    data: proximaManutencao,
                    backgroundColor: proximaManutencao.map(value => {
                        if (value < 1000) return this.colors.danger;
                        if (value < 2000) return this.colors.warning;
                        return this.colors.success;
                    }),
                    borderRadius: 8
                }]
            },
            options: {
                ...this.defaultOptions,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: (value) => value + ' km',
                            font: {
                                family: 'Inter'
                            }
                        }
                    }
                }
            }
        });
    },

    // Atualizar gráfico com novos dados
    updateChart(chart, newData) {
        if (!chart) return;
        
        chart.data.datasets.forEach((dataset, i) => {
            dataset.data = newData[i] || newData;
        });
        
        chart.update();
    },

    // Destruir gráfico
    destroyChart(chart) {
        if (chart) {
            chart.destroy();
        }
    }
};

// Expor globalmente
window.FrotaCharts = FrotaCharts;


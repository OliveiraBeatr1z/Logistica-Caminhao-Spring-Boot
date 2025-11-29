/**
 * FrotaLux - Mapas Interativos
 * Integração com Leaflet.js para rastreamento
 */

const FrotaMaps = {
    maps: {},
    markers: {},
    routes: {},
    updateIntervals: {},

    // Ícones customizados
    icons: {
        truck: L.divIcon({
            className: 'custom-truck-icon',
            html: '<div style="background: #1e3a8a; color: white; padding: 8px; border-radius: 50%; box-shadow: 0 2px 8px rgba(0,0,0,0.3);"><i class="fas fa-truck fa-lg"></i></div>',
            iconSize: [40, 40],
            iconAnchor: [20, 20]
        }),
        origin: L.divIcon({
            className: 'custom-origin-icon',
            html: '<div style="background: #10b981; color: white; padding: 8px; border-radius: 50%; box-shadow: 0 2px 8px rgba(0,0,0,0.3);"><i class="fas fa-map-marker-alt fa-lg"></i></div>',
            iconSize: [35, 35],
            iconAnchor: [17.5, 35]
        }),
        destination: L.divIcon({
            className: 'custom-dest-icon',
            html: '<div style="background: #ef4444; color: white; padding: 8px; border-radius: 50%; box-shadow: 0 2px 8px rgba(0,0,0,0.3);"><i class="fas fa-flag-checkered fa-lg"></i></div>',
            iconSize: [35, 35],
            iconAnchor: [17.5, 35]
        })
    },

    // Inicializar mapa
    initMap(containerId, center = [-23.5505, -46.6333], zoom = 12) {
        const map = L.map(containerId).setView(center, zoom);

        // Tile Layer - OpenStreetMap
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors',
            maxZoom: 19
        }).addTo(map);

        this.maps[containerId] = map;
        return map;
    },

    // Criar mapa de rastreamento de entrega
    initTrackingMap(containerId, solicitacaoId) {
        const map = this.initMap(containerId);

        // Buscar dados da solicitação
        this.loadTrackingData(solicitacaoId, map);

        // Atualizar posição a cada 30 segundos
        this.updateIntervals[solicitacaoId] = setInterval(() => {
            this.updateTruckPosition(solicitacaoId, map);
        }, 30000);

        return map;
    },

    // Carregar dados de rastreamento
    async loadTrackingData(solicitacaoId, map) {
        try {
            const response = await fetch(`/api/rastreamento/${solicitacaoId}/posicao`);
            const data = await response.json();

            // Limpar marcadores anteriores
            if (this.markers[solicitacaoId]) {
                this.markers[solicitacaoId].forEach(marker => marker.remove());
            }
            this.markers[solicitacaoId] = [];

            // Adicionar marcador de origem
            const originMarker = L.marker([data.origemLat, data.origemLon], {
                icon: this.icons.origin
            }).addTo(map);
            originMarker.bindPopup(`
                <div style="font-family: Inter, sans-serif;">
                    <strong>Origem</strong><br>
                    ${data.origemEndereco || 'Endereço de coleta'}
                </div>
            `);
            this.markers[solicitacaoId].push(originMarker);

            // Adicionar marcador de destino
            const destMarker = L.marker([data.destinoLat, data.destinoLon], {
                icon: this.icons.destination
            }).addTo(map);
            destMarker.bindPopup(`
                <div style="font-family: Inter, sans-serif;">
                    <strong>Destino</strong><br>
                    ${data.destinoEndereco || 'Endereço de entrega'}
                </div>
            `);
            this.markers[solicitacaoId].push(destMarker);

            // Adicionar marcador do caminhão (se houver posição)
            if (data.motoristaLat && data.motoristaLon) {
                const truckMarker = L.marker([data.motoristaLat, data.motoristaLon], {
                    icon: this.icons.truck
                }).addTo(map);
                truckMarker.bindPopup(`
                    <div style="font-family: Inter, sans-serif;">
                        <strong>Caminhão em trânsito</strong><br>
                        Motorista: ${data.motoristaNome || 'N/A'}<br>
                        Status: ${data.status || 'N/A'}<br>
                        <small>Última atualização: ${data.ultimaAtualizacao || 'N/A'}</small>
                    </div>
                `);
                this.markers[solicitacaoId].push(truckMarker);
            }

            // Desenhar rota
            this.drawRoute(map, solicitacaoId, [
                [data.origemLat, data.origemLon],
                [data.destinoLat, data.destinoLon]
            ], data.motoristaLat && data.motoristaLon ? [data.motoristaLat, data.motoristaLon] : null);

            // Ajustar zoom para mostrar todos os marcadores
            const bounds = L.latLngBounds([
                [data.origemLat, data.origemLon],
                [data.destinoLat, data.destinoLon]
            ]);
            if (data.motoristaLat && data.motoristaLon) {
                bounds.extend([data.motoristaLat, data.motoristaLon]);
            }
            map.fitBounds(bounds, { padding: [50, 50] });

            return data;
        } catch (error) {
            console.error('Erro ao carregar dados de rastreamento:', error);
            return null;
        }
    },

    // Desenhar rota no mapa
    drawRoute(map, solicitacaoId, points, currentPosition = null) {
        // Remover rota anterior
        if (this.routes[solicitacaoId]) {
            this.routes[solicitacaoId].remove();
        }

        // Rota completa (origem -> destino)
        const routeLine = L.polyline(points, {
            color: '#64748b',
            weight: 4,
            opacity: 0.6,
            dashArray: '10, 10'
        }).addTo(map);

        // Se houver posição atual, desenhar rota percorrida
        if (currentPosition) {
            const completedLine = L.polyline([points[0], currentPosition], {
                color: '#1e3a8a',
                weight: 5,
                opacity: 0.8
            }).addTo(map);
            
            this.routes[solicitacaoId] = L.layerGroup([routeLine, completedLine]).addTo(map);
        } else {
            this.routes[solicitacaoId] = routeLine;
        }
    },

    // Atualizar posição do caminhão
    async updateTruckPosition(solicitacaoId, map) {
        try {
            const response = await fetch(`/api/rastreamento/${solicitacaoId}/posicao`);
            const data = await response.json();

            if (data.motoristaLat && data.motoristaLon) {
                // Atualizar marcador do caminhão
                const truckMarkerIndex = this.markers[solicitacaoId].findIndex(m => 
                    m.getIcon() === this.icons.truck
                );

                if (truckMarkerIndex >= 0) {
                    this.markers[solicitacaoId][truckMarkerIndex].setLatLng([data.motoristaLat, data.motoristaLon]);
                } else {
                    const truckMarker = L.marker([data.motoristaLat, data.motoristaLon], {
                        icon: this.icons.truck
                    }).addTo(map);
                    this.markers[solicitacaoId].push(truckMarker);
                }

                // Atualizar rota
                this.drawRoute(map, solicitacaoId, [
                    [data.origemLat, data.origemLon],
                    [data.destinoLat, data.destinoLon]
                ], [data.motoristaLat, data.motoristaLon]);

                // Atualizar info card (se existir)
                this.updateInfoCard(data);
            }
        } catch (error) {
            console.error('Erro ao atualizar posição:', error);
        }
    },

    // Atualizar card de informações
    updateInfoCard(data) {
        const infoCard = document.getElementById('delivery-info-card');
        if (infoCard) {
            const statusBadge = infoCard.querySelector('.status-badge');
            const distanceText = infoCard.querySelector('.distance-text');
            const timeText = infoCard.querySelector('.time-text');

            if (statusBadge) {
                statusBadge.textContent = data.status || 'N/A';
                statusBadge.className = `badge badge-modern ${this.getStatusBadgeClass(data.status)}`;
            }

            if (distanceText && data.distanciaRestante) {
                distanceText.textContent = `${data.distanciaRestante.toFixed(1)} km`;
            }

            if (timeText && data.tempoEstimado) {
                timeText.textContent = data.tempoEstimado;
            }
        }
    },

    // Obter classe CSS para badge de status
    getStatusBadgeClass(status) {
        const classes = {
            'COLETA': 'badge-status-coleta',
            'EM_PROCESSAMENTO': 'badge-status-processamento',
            'A_CAMINHO': 'badge-status-caminho',
            'ENTREGUE': 'badge-status-entregue'
        };
        return classes[status] || '';
    },

    // Mapa com múltiplos motoristas (Dashboard)
    initFleetMap(containerId, motoristas) {
        const map = this.initMap(containerId, [-23.5505, -46.6333], 11);

        motoristas.forEach(motorista => {
            if (motorista.ultimaLatitude && motorista.ultimaLongitude) {
                const marker = L.marker([motorista.ultimaLatitude, motorista.ultimaLongitude], {
                    icon: this.icons.truck
                }).addTo(map);

                marker.bindPopup(`
                    <div style="font-family: Inter, sans-serif;">
                        <strong>${motorista.nome}</strong><br>
                        <small>CNH: ${motorista.cnh}</small><br>
                        <small>Última atualização: ${FrotaLux.timeAgo(motorista.ultimaAtualizacaoLocalizacao)}</small>
                    </div>
                `);
            }
        });

        return map;
    },

    // Parar atualizações automáticas
    stopUpdates(solicitacaoId) {
        if (this.updateIntervals[solicitacaoId]) {
            clearInterval(this.updateIntervals[solicitacaoId]);
            delete this.updateIntervals[solicitacaoId];
        }
    },

    // Limpar mapa
    clearMap(containerId) {
        const map = this.maps[containerId];
        if (map) {
            map.remove();
            delete this.maps[containerId];
        }
    },

    // Adicionar controle de tela cheia
    addFullscreenControl(map) {
        L.control.fullscreen({
            position: 'topleft',
            title: 'Tela cheia',
            titleCancel: 'Sair da tela cheia'
        }).addTo(map);
    }
};

// Expor globalmente
window.FrotaMaps = FrotaMaps;


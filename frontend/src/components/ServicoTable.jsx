import React, { useEffect, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Card } from 'primereact/card';
import { ProgressSpinner } from 'primereact/progressspinner';
import axios from 'axios';

export default function ServicoTable() {
  const [servicos, setServicos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    axios.get('http://localhost:8080/api/servicos')
      .then(res => {
        // HATEOAS: os dados vêm em _embedded ou content
        const data = res.data._embedded?.servicoList || res.data.content || [];
        setServicos(data);
        setLoading(false);
      })
      .catch(() => {
        setError('Erro ao carregar serviços.');
        setLoading(false);
      });
  }, []);

  if (loading) return <div className="flex justify-content-center"><ProgressSpinner/></div>;
  if (error) return <div className="p-error">{error}</div>;

  return (
    <Card title="Serviços Disponíveis">
      <DataTable value={servicos} paginator rows={10} responsiveLayout="scroll" stripedRows>
        <Column field="id" header="#" style={{ width: '4em' }} />
        <Column field="nome" header="Nome" />
        <Column field="descricao" header="Descrição" />
        <Column field="preco" header="Preço (€)" body={row => row.preco?.toFixed ? row.preco.toFixed(2) : row.preco} />
      </DataTable>
    </Card>
  );
}

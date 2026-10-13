interface Props {
  lastFetch: string | null;
}

export default function StatusFooter({ lastFetch }: Props) {
  const text = lastFetch
    ? new Intl.DateTimeFormat('hr-HR', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(lastFetch))
    : 'nepoznato';
  return <footer className="status-footer">Podaci ažurirani: {text}</footer>;
}
